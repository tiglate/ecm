package ludo.mentis.aciem.ecm.service.crypto.aes;

import jakarta.annotation.PreDestroy;
import ludo.mentis.aciem.ecm.exception.CryptoException;
import ludo.mentis.aciem.ecm.model.CipherEnvelope;
import ludo.mentis.aciem.ecm.model.Kdf;
import ludo.mentis.aciem.ecm.service.crypto.CryptoService;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Objects;

@Service
public final class AesService implements CryptoService {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;
    private static final int IV_LEN = 12;

    private final AesServiceConfig cfg;
    private final SecureRandom rng;

    public AesService(AesServiceConfig cfg) {
        this.cfg = Objects.requireNonNull(cfg, "cfg");
        try {
            this.rng = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("SecureRandom strong not available", e);
        }
    }

    @Override
    public CipherEnvelope encryptString(String plaintext) {
        return encrypt(plaintext.getBytes(StandardCharsets.UTF_8), null);
    }

    @Override
    public CipherEnvelope encryptString(String plaintext, byte[] aad) {
        return encrypt(plaintext.getBytes(StandardCharsets.UTF_8), aad);
    }

    @Override
    public CipherEnvelope encrypt(byte[] plaintext, byte[] aad) {
        try {
            byte[] iv = new byte[IV_LEN];
            rng.nextBytes(iv);

            if (cfg.passphrase != null) {
                byte[] salt = new byte[cfg.saltLengthBytes];
                rng.nextBytes(salt);
                SecretKey sk = deriveKey(cfg, salt, cfg.pbkdf2Iterations);
                byte[] ct = encryptAesGcm(sk, iv, plaintext, aad);
                return CipherEnvelope.builder()
                        .kdf(Kdf.PBKDF2)
                        .iterations(cfg.pbkdf2Iterations)
                        .salt(salt)
                        .iv(iv)
                        .ciphertext(ct)
                        .build();
            } else {
                ensureRawKey(cfg);
                byte[] ct = encryptAesGcm(cfg.secretKey, iv, plaintext, aad);
                return CipherEnvelope.builder()
                        .kdf(Kdf.RAW)
                        .iv(iv)
                        .ciphertext(ct)
                        .build();
            }
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Encryption failed", e);
        } finally {
            if (plaintext != null)
                java.util.Arrays.fill(plaintext, (byte) 0);
        }
    }

    @Override
    public byte[] decrypt(CipherEnvelope envelope, byte[] expectedAad) {
        try {
            Objects.requireNonNull(envelope, "envelope");

            final SecretKey sk;
            if (envelope.getKdf() == Kdf.PBKDF2) {
                if (cfg.passphrase == null) {
                    // You *can* decrypt even if this instance uses RAW mode,
                    // as long as you build a new AesService instance with a passphrase.
                    // Here we enforce consistency with this instance’s config.
                    throw new CryptoException("This service is not configured for PBKDF2 (passphrase missing)");
                }
                sk = deriveKey(cfg, envelope.getSalt(), envelope.getIterations());
            } else {
                ensureRawKey(cfg);
                sk = cfg.secretKey;
            }

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, sk, new GCMParameterSpec(GCM_TAG_BITS, envelope.getIv()));
            if (expectedAad != null)
                cipher.updateAAD(expectedAad);
            return cipher.doFinal(envelope.getCiphertext());
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new CryptoException("Decryption failed", e);
        }
    }

    @Override
    public String decryptToString(CipherEnvelope envelope) {
        byte[] out = decrypt(envelope, null);
        return new String(out, StandardCharsets.UTF_8);
    }

    @Override
    public String decryptToString(CipherEnvelope envelope, byte[] expectedAad) {
        byte[] out = decrypt(envelope, expectedAad);
        return new String(out, StandardCharsets.UTF_8);
    }

    /** Optional helper to generate a random AES key (for raw-key mode). */
    public SecretKey generateRandomKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(cfg.keyLengthBits, rng);
            return kg.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("AES KeyGenerator not available", e);
        }
    }

    @PreDestroy
    @Override
    public void close() {
        if (cfg.passphrase != null) {
            java.util.Arrays.fill(cfg.passphrase, '\0');
        }
    }

    // ==== Helpers ====

    private static SecretKey deriveKey(AesServiceConfig cfg, byte[] salt, int iterations) {
        try {
            KeySpec spec = new PBEKeySpec(cfg.passphrase, salt, iterations, cfg.keyLengthBits);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = f.generateSecret(spec).getEncoded();
            try {
                return new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");
            } finally {
                java.util.Arrays.fill(keyBytes, (byte) 0);
            }
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Key derivation failed", e);
        }
    }

    private static byte[] encryptAesGcm(SecretKey key, byte[] iv, byte[] plaintext, byte[] aad)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
        if (aad != null)
            cipher.updateAAD(aad);
        return cipher.doFinal(plaintext);
    }

    private static void ensureRawKey(AesServiceConfig cfg) {
        if (cfg.secretKey == null)
            throw new CryptoException("Expected raw key config");
        if (!"AES".equalsIgnoreCase(cfg.secretKey.getAlgorithm()))
            throw new CryptoException("SecretKey must be AES");
    }
}
