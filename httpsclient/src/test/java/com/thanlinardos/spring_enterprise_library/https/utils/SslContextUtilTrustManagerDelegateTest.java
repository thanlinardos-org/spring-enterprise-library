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
import static org.mockito.Mockito.when;

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
        X509TrustManager trustManager = mock(X509TrustManager.class);
        X509Certificate[] issuers = new X509Certificate[0];
        when(trustManager.getAcceptedIssuers()).thenReturn(issuers);
        SslContextUtil.TrustManagerDelegate delegate = new SslContextUtil.TrustManagerDelegate(trustManager, mock(TrustStrategy.class));

        assertArrayEquals(issuers, delegate.getAcceptedIssuers());
    }
}

