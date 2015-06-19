package yevgeniy.melnichuk.jmdns;

public class ServiceTypeBuilder {
    private String app = "_http";
    private String protocol = "_tcp";
    private String domain = "local";

    public String getType() {
        return String.format("%s.%s.%s.", app, protocol, domain);
    }

    public ServiceTypeBuilder withTcp() {
        this.protocol = addPrefixIfMissing("_tcp");
        return this;
    }

    public ServiceTypeBuilder withUdp() {
        this.protocol = addPrefixIfMissing("_udp");
        return this;
    }

    public ServiceTypeBuilder withDomain(final String domain) {
        this.domain = domain;
        return this;
    }

    public ServiceTypeBuilder withApp(final String app) {
        this.app = addPrefixIfMissing(app);
        return this;
    }

    public ServiceTypeBuilder withHttp() {
        this.app = addPrefixIfMissing("_http");
        return this;
    }

    public ServiceTypeBuilder withFtp() {
        this.app = addPrefixIfMissing("_ftp");
        return this;
    }

    private String addPrefixIfMissing(final String value) {
        if (value.startsWith("_")) {
            return value;
        }

        return "_" + value;
    }

}
