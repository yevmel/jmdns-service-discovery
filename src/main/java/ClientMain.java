import yevgeniy.melnichuk.jmdns.JMDNSServiceDiscovery;
import yevgeniy.melnichuk.jmdns.JMDNSServiceDiscovery.Service;
import yevgeniy.melnichuk.jmdns.JMDNSServiceDiscovery.ServiceCallback;
import yevgeniy.melnichuk.jmdns.ServiceTypeBuilder;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        String type = new ServiceTypeBuilder().withHttp().getType();
        JMDNSServiceDiscovery serviceDiscovery = new JMDNSServiceDiscovery().discover(type, new ServiceCallback() {

            @Override
            public void serviceAvailable(Service info) {
                System.out.println("service available: " + info.getName() + "(" + info.getSubtype() + "." + info.getType() + ")");
            }

            @Override
            public void serviceDisappeared(Service info) {
            }
        });
        Thread.sleep(30_000);

        serviceDiscovery.shutdown();
    }
}
