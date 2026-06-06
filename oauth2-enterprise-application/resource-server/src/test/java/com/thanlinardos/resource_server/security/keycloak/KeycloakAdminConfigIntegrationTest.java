package com.thanlinardos.resource_server.security.keycloak;

import com.thanlinardos.resource_server.model.properties.keycloak.KeycloakProperties;
import com.thanlinardos.spring_enterprise_library.annotations.SpringTest;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringTest
@TestPropertySource(properties = {
        "integration.test.enabled=false",
        "thanlinardos.springenterpriselibrary.oauth2.auth-server=KEYCLOAK"
})
class KeycloakAdminConfigIntegrationTest {

    @Autowired
    private ObjectProvider<RealmResource> keycloakRealmProvider;

    @Autowired
    private KeycloakProperties keycloakProperties;

    @Test
    void loadsKeycloakAdminBeans() {
        assertEquals("http://auth_server:9999", keycloakProperties.getUrl());
        assertEquals("realm", keycloakProperties.getRealm());
        assertNotNull(keycloakRealmProvider.getObject());
    }
}
