package yevgeniy.melnichuk.jmdns;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.NetworkTopologyDiscovery;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMDNSServiceDiscovery {
    static final private Logger LOGGER = LoggerFactory.getLogger(JMDNSServiceDiscovery.class);

    private List<JmDNS> jmdnsInstances;

    public void shutdown() throws Exception {
        for (JmDNS jmdns : jmdnsInstances) {
            jmdns.close();
        }
    }

    private void init() {
        InetAddress[] addresses = NetworkTopologyDiscovery.Factory.getInstance().getInetAddresses();
        for (InetAddress addr : addresses) {
            try {
                LOGGER.debug("init jmdns for iface " + addr);
                jmdnsInstances.add(JmDNS.create(addr));
            } catch (IOException e) {
                LOGGER.error("failed to init JMDNS.", e);
            }
        }
    }

    public JMDNSServiceDiscovery discover(String type, final Map<String, String> query, final ServiceCallback callback) {
        return discover(type, new ServiceCallback() {

            @Override
            public void serviceAvailable(Service info) {
                if (queryMatches(query, info)) {
                    callback.serviceAvailable(info);
                }
            }

            @Override
            public void serviceDisappeared(Service info) {
                if (queryMatches(query, info)) {
                    callback.serviceDisappeared(info);
                }
            }

            private boolean queryMatches(final Map<String, String> query, final Service info) {
                Map<String, String> properties = info.getProperties();
                for (Map.Entry<String, String> entry : query.entrySet()) {
                    String valueFromQuery = entry.getValue();
                    String key = entry.getKey();

                    // ignore empty queries
                    if (valueFromQuery == null) {
                        continue;
                    }

                    String valueFromService = properties.get(key);
                    if (!valueFromQuery.equals(valueFromService)) {
                        return false;
                    }
                }

                return true;
            }
        });
    }

    public JMDNSServiceDiscovery discover(String type, final ServiceCallback callback) {
        if (jmdnsInstances == null) {
            jmdnsInstances = new ArrayList<>();
            init();
        }

        for (JmDNS jmdns : jmdnsInstances) {
            jmdns.addServiceListener(type, new ServiceListener() {

                @Override
                public void serviceResolved(ServiceEvent event) {
                    String subtype = event.getInfo().getSubtype();
                    String type = event.getType();
                    String name = event.getName();
                    Map<String, String> props = getPropertiesFromServiceInfo(event.getInfo());

                    Service info = new Service().withType(type).withName(name).withProperties(props).withSubtype(subtype);
                    callback.serviceAvailable(info);
                }

                private Map<String, String> getPropertiesFromServiceInfo(ServiceInfo info) {
                    Map<String, String> properties = new HashMap<String, String>();

                    Enumeration<String> propertyNames = info.getPropertyNames();
                    while (propertyNames.hasMoreElements()) {
                        String key = (String) propertyNames.nextElement();
                        String value = info.getPropertyString(key);

                        properties.put(key, value);
                    }

                    return properties;
                }

                @Override
                public void serviceRemoved(ServiceEvent event) {
                    String subtype = event.getInfo().getSubtype();
                    String type = event.getType();
                    String name = event.getName();
                    Map<String, String> props = getPropertiesFromServiceInfo(event.getInfo());

                    Service info = new Service().withType(type).withName(name).withProperties(props).withSubtype(subtype);
                    callback.serviceDisappeared(info);
                }

                @Override
                public void serviceAdded(ServiceEvent event) {
                    // ...
                }
            });
        }

        return this;
    }

    public static class Service {
        private String name;
        private String type;
        private String subtype;
        private Map<String, String> properties;

        public String getName() {
            return name;
        }

        public Service withProperties(Map<String, String> props) {
            this.properties = props;
            return this;
        }

        public Service withName(String name) {
            this.name = name;
            return this;
        }

        public Service withType(String type) {
            this.type = type;
            return this;
        }

        public Service withSubtype(String subtype) {
            this.subtype = subtype;
            return this;
        }

        public Map<String, String> getProperties() {
            return Collections.unmodifiableMap(properties);
        }

        public String getType() {
            return type;
        }

        public String getSubtype() {
            return subtype;
        }

    }

    public static interface ServiceCallback {
        void serviceAvailable(Service info);

        void serviceDisappeared(Service info);
    }
}
