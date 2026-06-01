package com.thanlinardos.spring_enterprise_library.https.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.https.api.TrustStrategy;
import org.junit.jupiter.api.Test;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@CoreTest
class SslContextUtilTrustManagerDelegateTest {

    private static final String RSA = "RSA";

    @Test
    void checkClientTrusted_shouldDelegateToWrappedTrustManager() throws Exception {
        X509TrustManager trustManager = mock(X509TrustManager.class);
        TrustStrategy strategy = (chain, authType) -> false;
        SslContextUtil.TrustManagerDelegate delegate = new SslContextUtil.TrustManagerDelegate(trustManager, strategy);

        X509Certificate[] chain = new X509Certificate[0];
        delegate.checkClientTrusted(chain, RSA);

        verify(trustManager).checkClientTrusted(chain, RSA);
    }

    @Test
    void checkServerTrusted_shouldSkipDelegateWhenStrategyTrusts() throws Exception {
        X509TrustManager trustManager = mock(X509TrustManager.class);
        TrustStrategy strategy = (chain, authType) -> true;
        SslContextUtil.TrustManagerDelegate delegate = new SslContextUtil.TrustManagerDelegate(trustManager, strategy);

        delegate.checkServerTrusted(new X509Certificate[0], RSA);

        verify(trustManager, never()).checkServerTrusted(any(), anyString());
    }

    @Test
    void checkServerTrusted_shouldDelegateWhenStrategyDoesNotTrust() throws Exception {
        X509TrustManager trustManager = mock(X509TrustManager.class);
        TrustStrategy strategy = (chain, authType) -> false;
        SslContextUtil.TrustManagerDelegate delegate = new SslContextUtil.TrustManagerDelegate(trustManager, strategy);

        X509Certificate[] chain = new X509Certificate[0];
        delegate.checkServerTrusted(chain, RSA);

        verify(trustManager).checkServerTrusted(chain, RSA);
    }

    @Test
    void getAcceptedIssuers_shouldReturnWrappedTrustManagerIssuers() {
        X509TrustManager trustManager = new DummyX509TrustManager();
        X509Certificate[] issuers = new X509Certificate[0];
        SslContextUtil.TrustManagerDelegate delegate = new SslContextUtil.TrustManagerDelegate(trustManager, new DummyTrustStrategy());

        assertArrayEquals(issuers, delegate.getAcceptedIssuers());
    }

    private static class DummyX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // do nothing
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // do nothing
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class DummyTrustStrategy implements TrustStrategy {

        @Override
        public boolean isTrusted(X509Certificate[] chain, String authType) {
            return true;
        }
    }
}

