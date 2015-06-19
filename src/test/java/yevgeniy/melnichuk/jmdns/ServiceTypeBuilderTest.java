package yevgeniy.melnichuk.jmdns;

import static org.junit.Assert.*;

import org.junit.Test;

public class ServiceTypeBuilderTest {

    @Test
    public void shouldConstructTypeWithTcpHttpAndLocalDomain_whenLeftUnconfigured() {
        assertEquals("_http._tcp.local.", new ServiceTypeBuilder().getType());
    }

    @Test
    public void shouldSetAppToFtp() {
        assertTrue(new ServiceTypeBuilder().withFtp().getType().startsWith("_ftp."));
    }

    @Test
    public void shouldSetAppToHttp() {
        assertTrue(new ServiceTypeBuilder().withHttp().getType().startsWith("_http."));
    }

    @Test
    public void shouldSetProtocolToTcp() {
        assertTrue(new ServiceTypeBuilder().withTcp().getType().matches("_\\w+\\." + "_tcp" + "\\..+\\."));
    }

    @Test
    public void shouldSetApplicationToSpecifiedValue() {
        assertTrue(new ServiceTypeBuilder().withApp("foobar").getType().startsWith("_foobar."));
    }

    @Test
    public void shouldSetProtocolToUdp() {
        assertTrue(new ServiceTypeBuilder().withUdp().getType().matches("_\\w+\\." + "_udp" + "\\..+\\."));
    }

    @Test
    public void shoulSetDomain() {
        assertTrue(new ServiceTypeBuilder().withDomain("foo").getType().endsWith(".foo."));
    }

}
