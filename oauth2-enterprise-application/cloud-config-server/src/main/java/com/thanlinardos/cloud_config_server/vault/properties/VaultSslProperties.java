package com.thanlinardos.cloud_config_server.vault.properties;

import org.springframework.core.io.Resource;

/**
 * Nested SSL properties for the Vault backend.
 *
 * @param keyStore           classpath or file resource pointing to the client key store.
 * @param keyStorePassword   password for the key store.
 * @param trustStore         classpath or file resource pointing to the trust store.
 * @param trustStorePassword password for the trust store.
 */
public record VaultSslProperties(Resource keyStore, String keyStorePassword, Resource trustStore, String trustStorePassword) {
}