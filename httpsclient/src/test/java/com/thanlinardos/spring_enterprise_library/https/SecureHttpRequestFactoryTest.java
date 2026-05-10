package com.thanlinardos.spring_enterprise_library.https;

import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
import com.thanlinardos.spring_enterprise_library.https.properties.KeyAndTrustStoreProperties;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecureHttpRequestFactoryTest {

    @Test
    void prepareConnection_shouldConfigureHttpsConnection() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, null);

        TestSecureHttpRequestFactory factory = new TestSecureHttpRequestFactory(sslContext);
        TestHttpsURLConnection connection = new TestHttpsURLConnection(new URI("https://example.com").toURL());

        factory.callPrepare(connection);

        assertNotNull(connection.capturedSocketFactory);
        assertNotNull(connection.capturedHostnameVerifier);
        assertFalse(connection.capturedHostnameVerifier.verify("example.com", null));
    }

    @Test
    void prepareConnection_shouldAlsoWorkForPlainHttpConnections() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, null);

        TestSecureHttpRequestFactory factory = new TestSecureHttpRequestFactory(sslContext);
        HttpURLConnection connection = (HttpURLConnection) new URI("http://example.com").toURL().openConnection(Proxy.NO_PROXY);

        assertDoesNotThrow(() -> factory.callPrepare(connection));
    }

    @Test
    void constructor_withInvalidStoreData_shouldWrapAsCoreException() {
        KeyAndTrustStoreProperties invalidStore = new KeyAndTrustStoreProperties(new ByteArrayResource("invalid".getBytes()), "pass");

        assertThrows(CoreException.class, () -> new SecureHttpRequestFactory(invalidStore, invalidStore));
    }

    private static final class TestSecureHttpRequestFactory extends SecureHttpRequestFactory {
        private TestSecureHttpRequestFactory(SSLContext sslContext) {
            super(sslContext);
        }

        private void callPrepare(HttpURLConnection connection) throws IOException {
            super.prepareConnection(connection, "GET");
        }
    }

    private static final class TestHttpsURLConnection extends HttpsURLConnection {

        private SSLSocketFactory capturedSocketFactory;
        private HostnameVerifier capturedHostnameVerifier;

        private TestHttpsURLConnection(URL url) {
            super(url);
        }

        @Override
        public void setSSLSocketFactory(SSLSocketFactory sf) {
            this.capturedSocketFactory = sf;
        }

        @Override
        public void setHostnameVerifier(HostnameVerifier v) {
            this.capturedHostnameVerifier = v;
        }

        @Override
        public String getCipherSuite() {
            return null;
        }

        @Override
        public Certificate[] getLocalCertificates() {
            return null;
        }

        @Override
        public Certificate[] getServerCertificates() {
            return null;
        }

        @Override
        public Principal getPeerPrincipal() {
            return null;
        }

        @Override
        public Principal getLocalPrincipal() {
            return null;
        }

        @Override
        public void disconnect() {
            // do nothing
        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() {
            // do nothing
        }
    }
}

