# jmdns-service-discovery
makes using [jmdns](https://github.com/openhab/jmdns) more convenient.

## register services
```java
String type = new ServiceTypeBuilder().withHttp().getType();
JMDNSServicePromoter servicePromoter = new JMDNSServicePromoter();

// promote service named "test" with description "test-service" listening on port 6001
servicePromoter.promote(type, "test", "test-service", 6001);

// ...
servicePromoter.shutdown();
```

## listen for services
```java
String type = new ServiceTypeBuilder().withHttp().getType();

JMDNSServiceDiscovery serviceDiscovery = new JMDNSServiceDiscovery();
serviceDiscovery.discover(type, new ServiceCallback() {
    @Override
    public void serviceAvailable(Service info) {
        // ...
    }
});

// ...
serviceDiscovery.shutdown();
```
