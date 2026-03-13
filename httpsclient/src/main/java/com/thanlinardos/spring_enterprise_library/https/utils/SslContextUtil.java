package com.thanlinardos.spring_enterprise_library.https.utils;

import com.thanlinardos.spring_enterprise_library.https.api.TrustStrategy;
import com.thanlinardos.spring_enterprise_library.https.properties.KeyAndTrustStoreProperties;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Utility class for building SSL contexts and REST clients with custom keystore and truststore configurations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SslContextUtil {

    /**
     * The default type of the keystore and truststore.
     */
    private static final String KEY_STORE_TYPE = "PKCS12";
    private static final String TLS = "TLS";

    /**
     * Builds a RESTEasy client with the specified keystore and truststore properties.
     *
     * @param keystore   the keystore properties
     * @param truststore the truststore properties
     * @return a configured RESTEasy client
     * @throws UnrecoverableKeyException if the key cannot be recovered from the keystore
     * @throws CertificateException      if there is an error with the certificates
     * @throws KeyStoreException         if there is an error with the keystore
     * @throws IOException               if there is an I/O error
     * @throws NoSuchAlgorithmException  if the algorithm for key management is not available
     * @throws KeyManagementException    if there is an error managing keys
     */
    public static Client buildResteasyClient(KeyAndTrustStoreProperties keystore, KeyAndTrustStoreProperties truststore) throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        return ClientBuilder.newBuilder()
                .sslContext(buildSSLContext(keystore, truststore))
                .build();
    }

    /**
     * Builds an SSL context with the specified keystore and truststore properties.
     *
     * @param keystore   the keystore properties
     * @param truststore the truststore properties
     * @return a configured SSL context
     * @throws KeyStoreException         if there is an error with the keystore
     * @throws IOException               if there is an I/O error
     * @throws NoSuchAlgorithmException  if the algorithm for key management is not available
     * @throws CertificateException      if there is an error with the certificates
     * @throws KeyManagementException    if there is an error managing keys
     * @throws UnrecoverableKeyException if the key cannot be recovered from the keystore
     */
    public static SSLContext buildSSLContext(KeyAndTrustStoreProperties keystore, KeyAndTrustStoreProperties truststore) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {
        KeyStore truststoreObject = null;
        if (truststore.isEnabled()) {
            truststoreObject = KeyStore.getInstance(KEY_STORE_TYPE);
            initializeTruststore(truststore, truststoreObject);
        }
        KeyStore keystoreObject = null;
        char[] keystorePassword = null;
        if (keystore.isEnabled()) {
            keystoreObject = KeyStore.getInstance(KEY_STORE_TYPE);
            keystorePassword = initializeKeystore(keystore, keystoreObject);
        }

        KeyManager[] keyManagers = loadKeyMaterial(keystoreObject, keystorePassword);
        TrustManager[] trustManagers = loadTrustMaterial(truststoreObject, (chain, authType) -> true);

        SSLContext sslContext = SSLContext.getInstance(TLS);
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private static TrustManager[] loadTrustMaterial(KeyStore truststore, TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(truststore);

        return Arrays.stream(trustManagerFactory.getTrustManagers())
                .filter(X509TrustManager.class::isInstance)
                .map(X509TrustManager.class::cast)
                .map(tm -> new TrustManagerDelegate(tm, trustStrategy)).distinct()
                .toArray(TrustManager[]::new);
    }

    private static KeyManager[] loadKeyMaterial(KeyStore keystore, char[] keyPassword) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, keyPassword);
        return kmfactory.getKeyManagers();
    }

    static class TrustManagerDelegate implements X509TrustManager {

        private final X509TrustManager trustManager;
        private final TrustStrategy trustStrategy;

        TrustManagerDelegate(X509TrustManager trustManager, TrustStrategy trustStrategy) {
            this.trustManager = trustManager;
            this.trustStrategy = trustStrategy;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.trustManager.checkClientTrusted(chain, authType);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if (!this.trustStrategy.isTrusted(chain, authType)) {
                this.trustManager.checkServerTrusted(chain, authType);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return this.trustManager.getAcceptedIssuers();
        }

    }

    private static char[] initializeKeystore(KeyAndTrustStoreProperties keystore, KeyStore keyStoreObject) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        char[] keyStorePassword;
        keyStorePassword = keystore.password().toCharArray();
        keyStoreObject.load(keystore.path().getInputStream(), keyStorePassword);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStoreObject, keyStorePassword);
        return keyStorePassword;
    }

    private static void initializeTruststore(KeyAndTrustStoreProperties truststore, KeyStore trustStoreObject) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        trustStoreObject.load(truststore.path().getInputStream(), truststore.password().toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStoreObject);
    }
}
