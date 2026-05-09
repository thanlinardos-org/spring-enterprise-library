package com.thanlinardos.cloud_config_server.vault.properties.batch;

import com.thanlinardos.spring_enterprise_library.integrations.vault.VaultIntegrationConstants;
import com.thanlinardos.cloud_config_server.vault.properties.VaultServerConfigProperties;
import com.thanlinardos.spring_enterprise_library.batch.properties.BatchRunTimerConfigProperties;
import com.thanlinardos.spring_enterprise_library.batch.properties.BatchSchedulerConfig;
import lombok.Getter;
import org.springframework.web.client.RestTemplate;

/**
 * Full configuration for the Vault sync job. Extends {@link BatchSchedulerConfig} directly
 * with all scheduler/task-execution values, and holds Vault connection data as plain fields.
 */
@Getter
public class VaultSyncJobConfig extends BatchSchedulerConfig {

    private static final String VAULT_SYNC_JOB = "VAULT_SYNC_JOB";

    /**
     * The name of the config server as registered in Vault.
     */
    private final String configServerName;
    /**
     * The token used to authenticate with Vault.
     */
    private final String token;
    /**
     * The maximum percentage of the lease duration after which a secret should be renewed.
     * For example, if a secret has a lease duration of 60 minutes and the max lease expiry percent is set to 80%,
     * the secret will be renewed after 48 minutes (=0.8 * 60).
     */
    private final int maxLeaseExpiryPercent;
    private final RestTemplate restTemplate;
    private final String vaultUrl;

    public VaultSyncJobConfig(String configServerName, VaultServerConfigProperties vaultProps, VaultSyncConfigProperties syncProps, BatchRunTimerConfigProperties runTimerProps, RestTemplate restTemplate) {
        super(syncProps, runTimerProps);
        this.configServerName = configServerName;
        this.vaultUrl = String.format(VaultIntegrationConstants.VAULT_URL_FORMAT, vaultProps.scheme(), vaultProps.host(), vaultProps.port());
        this.token = vaultProps.token();
        this.maxLeaseExpiryPercent = syncProps.maxLeaseExpiryPercent();
        this.restTemplate = restTemplate;
    }

    @Override
    public String getName() {
        return VAULT_SYNC_JOB;
    }
}
