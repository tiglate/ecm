package ludo.mentis.aciem.ecm.service.crypto.aes;

import ludo.mentis.aciem.ecm.exception.CryptoException;
import ludo.mentis.aciem.ecm.model.CipherEnvelope;
import ludo.mentis.aciem.ecm.model.Kdf;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.jupiter.api.Assertions.*;

class AesServiceTest {

    @Test
    void encryptDecrypt_roundTrip_passphrase_noAAD() {
        char[] pass = "sup3rS3cret".toCharArray();
        AesServiceConfig cfg = AesServiceConfig.builder()
                .passphrase(pass)
                .pbkdf2Iterations(210_000)
                .saltLengthBytes(16)
                .keyLengthBits(256)
                .build();
        AesService svc = new AesService(cfg);

        String text = "Hello AES";
        CipherEnvelope env = svc.encryptString(text);

        assertEquals(Kdf.PBKDF2, env.getKdf());
        assertNotNull(env.getSalt());
        assertTrue(env.getSalt().length >= 16);
        assertNotNull(env.getIv());
        assertNotNull(env.getCiphertext());

        String out = svc.decryptToString(env);
        assertEquals(text, out);
        svc.close();
    }

    @Test
    void encryptDecrypt_roundTrip_passphrase_withAAD_and_wrongAAD_fails() {
        char[] pass = "anotherS3cret".toCharArray();
        AesServiceConfig cfg = AesServiceConfig.builder()
                .passphrase(pass)
                .pbkdf2Iterations(210_000)
                .saltLengthBytes(16)
                .keyLengthBits(256)
                .build();
        AesService svc = new AesService(cfg);

        byte[] aad = "context".getBytes();
        CipherEnvelope env = svc.encryptString("Msg", aad);
        assertDoesNotThrow(() -> svc.decryptToString(env, aad));

        byte[] wrongAad = "other".getBytes();
        assertThrows(CryptoException.class, () -> svc.decryptToString(env, wrongAad));
        svc.close();
    }

    @Test
    void encryptDecrypt_roundTrip_rawKey() {
        byte[] keyBytes = new byte[32];
        for (int i = 0; i < keyBytes.length; i++) keyBytes[i] = (byte) (i * 3);
        SecretKey sk = new SecretKeySpec(keyBytes, "AES");
        AesServiceConfig cfg = AesServiceConfig.builder()
                .secretKey(sk)
                .keyLengthBits(256)
                .build();
        AesService svc = new AesService(cfg);

        CipherEnvelope env = svc.encrypt("DATA".getBytes(), null);
        assertEquals(Kdf.RAW, env.getKdf());
        byte[] out = svc.decrypt(env, null);
        assertArrayEquals("DATA".getBytes(), out);
        svc.close();
    }

    @Test
    void decrypt_with_service_in_wrong_mode_throws() {
        // Encrypt with passphrase service
        char[] pass = "yyy".toCharArray();
        AesServiceConfig cfgPass = AesServiceConfig.builder()
                .passphrase(pass)
                .pbkdf2Iterations(210_000)
                .build();
        AesService svcPass = new AesService(cfgPass);
        CipherEnvelope env = svcPass.encryptString("hello");
        svcPass.close();

        // Now try to decrypt with RAW-key service -> should throw
        byte[] keyBytes = new byte[16];
        SecretKey sk = new SecretKeySpec(keyBytes, "AES");
        AesServiceConfig cfgRaw = AesServiceConfig.builder()
                .secretKey(sk)
                .keyLengthBits(128)
                .build();
        AesService svcRaw = new AesService(cfgRaw);
        assertThrows(CryptoException.class, () -> svcRaw.decryptToString(env));
        svcRaw.close();
    }

    @Test
    void close_zeroizes_passphrase() {
        char[] pass = "will-be-zeroed".toCharArray();
        AesServiceConfig cfg = AesServiceConfig.builder()
                .passphrase(pass)
                .build();
        AesService svc = new AesService(cfg);
        svc.close();
        for (char c : pass) {
            assertEquals('\0', c, "passphrase char should be zeroed after close()");
        }
    }

    @Test
    void generateRandomKey_correctAlgorithm_and_length() {
        char[] pass = "zzz".toCharArray();
        AesServiceConfig cfg = AesServiceConfig.builder()
                .passphrase(pass)
                .keyLengthBits(192)
                .build();
        AesService svc = new AesService(cfg);
        SecretKey key = svc.generateRandomKey();
        assertEquals("AES", key.getAlgorithm());
        assertEquals(192 / 8, key.getEncoded().length);
        svc.close();
    }

    @Test
    void encrypt_byteArray_input_is_wiped_after_use() {
        char[] pass = "wipe-test".toCharArray();
        AesServiceConfig cfg = AesServiceConfig.builder()
                .passphrase(pass)
                .build();
        AesService svc = new AesService(cfg);
        byte[] data = "to-wipe".getBytes();
        svc.encrypt(data, null);
        // After encrypt, plaintext array should be zeroed
        for (byte b : data) assertEquals(0, b);
        svc.close();
    }
}
