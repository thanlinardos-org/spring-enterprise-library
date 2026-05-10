package com.thanlinardos.spring_enterprise_library.https.properties;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CoreTest
class KeyAndTrustStorePropertiesTest {

    @Test
    void isEnabled_shouldReturnTrueWhenPathAndPasswordExist() {
        KeyAndTrustStoreProperties props = new KeyAndTrustStoreProperties(new ByteArrayResource(new byte[0]), "secret");

        assertTrue(props.isEnabled());
    }

    @Test
    void isEnabled_shouldReturnFalseWhenPathMissing() {
        KeyAndTrustStoreProperties props = new KeyAndTrustStoreProperties(null, "secret");

        assertFalse(props.isEnabled());
    }

    @Test
    void isEnabled_shouldReturnFalseWhenPasswordMissing() {
        KeyAndTrustStoreProperties props = new KeyAndTrustStoreProperties(new ByteArrayResource(new byte[0]), null);

        assertFalse(props.isEnabled());
    }
}

