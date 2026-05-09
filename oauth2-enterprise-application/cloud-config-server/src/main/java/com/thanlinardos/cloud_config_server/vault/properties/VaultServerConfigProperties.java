package com.thanlinardos.cloud_config_server.vault.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Typed binding for the Vault backend connection settings.
 *
 * @param token  Vault authentication token.
 * @param host   Hostname of the Vault server.
 * @param port   Port of the Vault server.
 * @param scheme URL scheme ({@code http} or {@code https}).
 * @param ssl    Nested SSL (key-store / trust-store) configuration.
 */
@ConfigurationProperties(prefix = "spring.cloud.config.server.vault")
public record VaultServerConfigProperties(
        String token,
        String host,
        String port,
        String scheme,
        @NestedConfigurationProperty VaultSslProperties ssl) {
}