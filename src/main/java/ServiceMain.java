import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yevgeniy.melnichuk.jmdns.JMDNSServicePromoter;
import yevgeniy.melnichuk.jmdns.ServiceTypeBuilder;

public class ServiceMain {
    static final private Logger LOGGER = LoggerFactory.getLogger(ServiceMain.class);

    public static void main(String[] args) throws Exception {
        Map<String, String> props = new HashMap<String, String>();
        props.put("service", "actors-app");

        String type = new ServiceTypeBuilder().withTcp().withApp("moviedatabase").getType();
        JMDNSServicePromoter servicePromoter = new JMDNSServicePromoter().promote(type, "test", "test-service", 6001, props);
        LOGGER.info("announced services.");

        Thread.sleep(30_000);

        LOGGER.info("shutting down services.");
        servicePromoter.shutdown();
    }

}
