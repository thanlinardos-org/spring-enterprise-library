package com.thanlinardos.spring_enterprise_library.integrations.vault;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class VaultIntegrationHelperTest {

    private static final String MY_APP = "my-app";
    private static final String TOKEN_123 = "token-123";

    @Test
    void getAppEnvName_shouldJoinApplicationAndEnvironment() {
        assertEquals("my-app/prod", VaultIntegrationHelper.getAppEnvName(MY_APP, "prod"));
    }

    @Test
    void buildVaultUrlPath_shouldJoinBaseAndConfigPath() {
        assertEquals("https://vault.local/kv/data/my-app/dev",
                VaultIntegrationHelper.buildVaultUrlPath("kv/data/my-app/dev", "https://vault.local"));
    }

    @Test
    void buildVaultUrlPath_withAppAndEnvironment_shouldBuildFullPath() {
        assertEquals("https://vault.local/kv/data/my-app/dev",
                VaultIntegrationHelper.buildVaultUrlPath(MY_APP, "dev", "https://vault.local"));
    }

    @Test
    void buildAppConfigPath_withEnvironment_shouldIncludeEnvironment() {
        assertEquals("kv/data/my-app/dev", VaultIntegrationHelper.buildAppConfigPath(MY_APP, "dev"));
    }

    @Test
    void buildAppConfigPath_withNullEnvironment_shouldReturnAppOnlyPath() {
        assertEquals("kv/data/my-app", VaultIntegrationHelper.buildAppConfigPath(MY_APP, null));
    }

    @Test
    void setupVaultHttpHeaders_modifying_shouldSetTokenAndContentType() {
        HttpHeaders headers = VaultIntegrationHelper.setupVaultHttpHeaders(true, TOKEN_123);

        assertEquals(TOKEN_123, headers.getFirst(VaultIntegrationConstants.X_VAULT_TOKEN_HEADER));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    @Test
    void setupVaultHttpHeaders_nonModifying_shouldSetTokenOnly() {
        HttpHeaders headers = VaultIntegrationHelper.setupVaultHttpHeaders(false, TOKEN_123);

        assertEquals(TOKEN_123, headers.getFirst(VaultIntegrationConstants.X_VAULT_TOKEN_HEADER));
        assertNull(headers.getContentType());
    }

    @Test
    void setupVaultGetHttpEntity_shouldBuildEntityWithTokenHeader() {
        HttpEntity<Object> entity = VaultIntegrationHelper.setupVaultGetHttpEntity(TOKEN_123);

        assertEquals(TOKEN_123, entity.getHeaders().getFirst(VaultIntegrationConstants.X_VAULT_TOKEN_HEADER));
        assertNull(entity.getBody());
    }
}

