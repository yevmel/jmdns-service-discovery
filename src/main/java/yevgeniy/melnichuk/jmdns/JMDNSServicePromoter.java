package yevgeniy.melnichuk.jmdns;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.NetworkTopologyDiscovery;
import javax.jmdns.ServiceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMDNSServicePromoter {
    static final private Logger LOGGER = LoggerFactory.getLogger(JMDNSServicePromoter.class);

    private List<JmDNS> jmdnsInstances;

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

    public void shutdown() throws Exception {
        for (JmDNS jmdns : jmdnsInstances) {
            jmdns.unregisterAllServices();
            jmdns.close();
        }

    }

    public JMDNSServicePromoter promote(final String type, final String name, final String description, final int port) {
        Map<String, String> props = Collections.emptyMap();
        return promote(type, name, description, port, props);
    }

    public JMDNSServicePromoter promote(final String type, final String name, final String description, final int port, final Map<String, String> props) {
        if (jmdnsInstances == null) {
            jmdnsInstances = new ArrayList<>();
            init();
        }

        new Thread(new Runnable() {
            public void run() {
                try {
                    for (JmDNS jmdns : jmdnsInstances) {
                        ServiceInfo info = ServiceInfo.create(type, name, port, description);
                        info.setText(props);

                        jmdns.registerService(info);
                    }
                } catch (IOException e) {
                    LOGGER.error("failed to register service " + name, e);
                }
            }
        }).start();
        return this;
    }

}
