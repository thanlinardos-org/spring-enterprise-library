package com.thanlinardos.cloud_config_server.vault.properties.batch;

import com.thanlinardos.spring_enterprise_library.batch.properties.api.BatchSyncProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed binding for Vault sync job execution settings.
 */
@ConfigurationProperties(prefix = "batch.vault-sync")
public record VaultSyncConfigProperties(
        int backoffStepSize,
        int maxDelay,
        int maxTaskRetries,
        int maxExecutionAttempts,
        @Min(0) @Max(100) int maxLeaseExpiryPercent,
        boolean runOnStartup) implements BatchSyncProperties {
}