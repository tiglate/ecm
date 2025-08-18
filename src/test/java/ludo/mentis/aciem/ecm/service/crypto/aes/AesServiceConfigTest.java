package ludo.mentis.aciem.ecm.service.crypto.aes;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.jupiter.api.Assertions.*;

class AesServiceConfigTest {

    @Test
    void build_withPassphrase_only_ok() {
        char[] pwd = "secret-password".toCharArray();
        AesServiceConfig cfg = AesServiceConfig.builder()
                .passphrase(pwd)
                .pbkdf2Iterations(210_000)
                .saltLengthBytes(16)
                .keyLengthBits(256)
                .build();
        assertNotNull(cfg);
    }

    @Test
    void build_withSecretKey_only_ok() {
        byte[] keyBytes = new byte[32];
        for (int i = 0; i < keyBytes.length; i++) keyBytes[i] = (byte) i;
        SecretKey sk = new SecretKeySpec(keyBytes, "AES");
        AesServiceConfig cfg = AesServiceConfig.builder()
                .secretKey(sk)
                .keyLengthBits(256)
                .build();
        assertNotNull(cfg);
    }

    @Test
    void build_withBoth_throws() {
        byte[] keyBytes = new byte[16];
        SecretKey sk = new SecretKeySpec(keyBytes, "AES");
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                AesServiceConfig.builder()
                        .passphrase("x".toCharArray())
                        .secretKey(sk)
                        .build());
        assertTrue(ex.getMessage().toLowerCase().contains("exactly one"));
    }

    @Test
    void build_withNone_throws() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                AesServiceConfig.builder().build());
        assertTrue(ex.getMessage().toLowerCase().contains("passphrase")
                || ex.getMessage().toLowerCase().contains("secretkey"));
    }

    @Test
    void pbkdf2Iterations_tooLow_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                AesServiceConfig.builder().pbkdf2Iterations(99_999));
        assertTrue(ex.getMessage().toLowerCase().contains("iterations"));
    }

    @Test
    void saltLength_tooLow_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                AesServiceConfig.builder().saltLengthBytes(8));
        assertTrue(ex.getMessage().toLowerCase().contains("salt"));
    }

    @Test
    void keyLength_invalid_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                AesServiceConfig.builder().keyLengthBits(64));
        assertTrue(ex.getMessage().toLowerCase().contains("aes key length"));
    }
}
