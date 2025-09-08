package ludo.mentis.aciem.ecm.util;

import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ApiKeyUtilsImplTest {

    private final ApiKeyUtilsImpl utils = new ApiKeyUtilsImpl();

    @Test
    void generateApiKey_notNullOrEmpty() {
        String key = utils.generateApiKey();
        assertThat(key).isNotNull().isNotEmpty();
    }

    @Test
    void generateApiKey_returns64CharUrlSafeBase64WithoutPadding() {
        String key = utils.generateApiKey();
        // 48 bytes -> 64 Base64 chars; URL-safe alphabet; no padding '='
        assertThat(key)
                .hasSize(64)
                .matches("[A-Za-z0-9_-]{64}")
                .doesNotContain("=")
                .doesNotContain("+").doesNotContain("/");
    }

    @Test
    void generateApiKey_decodesTo48Bytes() {
        String key = utils.generateApiKey();
        byte[] decoded = Base64.getUrlDecoder().decode(key);
        assertThat(decoded).hasSize(48);
    }

    @Test
    void generateApiKey_returnsUniqueValuesAcrossCalls() {
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String key = utils.generateApiKey();
            // ensure constraints hold for each as well
            assertThat(key).matches("[A-Za-z0-9_-]{64}");
            keys.add(key);
        }
        assertThat(keys).hasSize(100);
    }
}
