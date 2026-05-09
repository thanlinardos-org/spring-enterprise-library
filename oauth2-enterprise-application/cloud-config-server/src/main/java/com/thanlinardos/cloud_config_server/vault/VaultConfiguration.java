package com.thanlinardos.cloud_config_server.vault;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanlinardos.spring_enterprise_library.batch.properties.BatchRunTimerConfigProperties;
import com.thanlinardos.cloud_config_server.vault.properties.VaultServerConfigProperties;
import com.thanlinardos.cloud_config_server.vault.properties.VaultSslProperties;
import com.thanlinardos.cloud_config_server.vault.properties.batch.VaultSyncConfigProperties;
import com.thanlinardos.cloud_config_server.vault.properties.batch.VaultSyncJobConfig;
import com.thanlinardos.spring_enterprise_library.https.SecureHttpRequestFactory;
import com.thanlinardos.spring_enterprise_library.https.properties.KeyAndTrustStoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({VaultServerConfigProperties.class, VaultSyncConfigProperties.class, BatchRunTimerConfigProperties.class})
@Slf4j
public class VaultConfiguration {

    /** {@code spring.application.name} does not share a prefix with any other property group here,
     *  so it is kept as a single {@code @Value} rather than a dedicated config-properties class. */
    @Value("${spring.application.name}")
    private String configServerName;

    @Bean
    @RefreshScope
    public VaultSyncJob vaultSyncJob(VaultServerConfigProperties vaultProps, VaultSyncConfigProperties syncProps, BatchRunTimerConfigProperties runTimerProps,
                                     ClientHttpRequestFactory vaultClientRequestFactory, ThreadPoolTaskScheduler taskScheduler, ObjectMapper objectMapper) {
        VaultSyncJobConfig config = buildVaultSyncJobConfig(vaultProps, syncProps, runTimerProps, vaultClientRequestFactory);
        return new VaultSyncJob(taskScheduler, config, objectMapper);
    }

    private VaultSyncJobConfig buildVaultSyncJobConfig(VaultServerConfigProperties vaultProps, VaultSyncConfigProperties syncProps, BatchRunTimerConfigProperties runTimerProps, ClientHttpRequestFactory vaultClientRequestFactory) {
        return new VaultSyncJobConfig(configServerName, vaultProps, syncProps, runTimerProps, new RestTemplate(vaultClientRequestFactory));
    }

    @Bean
    @RefreshScope
    public ClientHttpRequestFactory vaultClientRequestFactory(VaultServerConfigProperties vaultProps) {
        VaultSslProperties sslProperties = vaultProps.ssl();
        KeyAndTrustStoreProperties keystore = new KeyAndTrustStoreProperties(sslProperties.keyStore(), sslProperties.keyStorePassword());
        KeyAndTrustStoreProperties truststore = new KeyAndTrustStoreProperties(sslProperties.trustStore(), sslProperties.trustStorePassword());
        log.info("Creating vault client HTTP request factory with keystore {} and truststore {}", keystore.path().getFilename(), truststore.path().getFilename());
        return new SecureHttpRequestFactory(keystore, truststore);
    }
}