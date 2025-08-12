package ludo.mentis.aciem.ecm.model;

import ludo.mentis.aciem.ecm.exception.CryptoException;

import java.util.Base64;
import java.util.Objects;

/**
 * Portable, self-describing envelope for AES-GCM payloads.
 * String form (toString) is:
 * v1:pbkdf2:<iters>:<b64(salt)>:<b64(iv)>:<b64(cipher+tag)>
 * or for raw key mode: v1:raw::<b64(iv)>:<b64(cipher+tag)>
 */
public final class CipherEnvelope {

    private final String version; // e.g., "v1"
    private final Kdf kdf; // PBKDF2 or RAW
    private final Integer iterations; // only for PBKDF2
    private final byte[] salt; // only for PBKDF2
    private final byte[] iv; // 12 bytes recommended for GCM
    private final byte[] ciphertext; // ciphertext + GCM tag

    private CipherEnvelope(String version, Kdf kdf, Integer iterations, byte[] salt, byte[] iv, byte[] ciphertext) {
        this.version = Objects.requireNonNull(version, "version");
        this.kdf = Objects.requireNonNull(kdf, "kdf");
        this.iterations = iterations; // nullable for RAW
        this.salt = salt; // nullable for RAW
        this.iv = Objects.requireNonNull(iv, "iv");
        this.ciphertext = Objects.requireNonNull(ciphertext, "ciphertext");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String version = "v1";
        private Kdf kdf;
        private Integer iterations;
        private byte[] salt;
        private byte[] iv;
        private byte[] ciphertext;

        public Builder version(String v) {
            this.version = v;
            return this;
        }

        public Builder kdf(Kdf k) {
            this.kdf = k;
            return this;
        }

        public Builder iterations(Integer it) {
            this.iterations = it;
            return this;
        }

        public Builder salt(byte[] s) {
            this.salt = s;
            return this;
        }

        public Builder iv(byte[] i) {
            this.iv = i;
            return this;
        }

        public Builder ciphertext(byte[] c) {
            this.ciphertext = c;
            return this;
        }

        public CipherEnvelope build() {
            if (kdf == null)
                throw new IllegalStateException("kdf is required");
            if (kdf == Kdf.PBKDF2 && (iterations == null || iterations < 1))
                throw new IllegalStateException("pbkdf2 iterations required");
            if (kdf == Kdf.PBKDF2 && (salt == null || salt.length < 16))
                throw new IllegalStateException("pbkdf2 salt >= 16 bytes required");
            if (iv == null || iv.length == 0)
                throw new IllegalStateException("iv required");
            if (ciphertext == null || ciphertext.length == 0)
                throw new IllegalStateException("ciphertext required");
            return new CipherEnvelope(version, kdf, iterations, salt, iv, ciphertext);
        }
    }

    public String getVersion() {
        return version;
    }

    public Kdf getKdf() {
        return kdf;
    }

    public Integer getIterations() {
        return iterations;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getCiphertext() {
        return ciphertext;
    }

    /** Compact textual form handy for logs or DB columns. */
    @Override
    public String toString() {
        Base64.Encoder enc = Base64.getEncoder();
        if (kdf == Kdf.PBKDF2) {
            return String.join(":",
                    version,
                    "pbkdf2",
                    Integer.toString(iterations),
                    enc.encodeToString(salt),
                    enc.encodeToString(iv),
                    enc.encodeToString(ciphertext));
        } else {
            return String.join(":",
                    version,
                    "raw",
                    enc.encodeToString(iv),
                    enc.encodeToString(ciphertext));
        }
    }

    /** Parse the string form back into an object. */
    public static CipherEnvelope parse(String s) {
        try {
            String[] parts = s.split(":");
            if (parts.length < 4)
                throw new CryptoException("Invalid envelope");

            String version = parts[0];
            String kdfStr = parts[1];

            Base64.Decoder dec = Base64.getDecoder();

            if ("pbkdf2".equalsIgnoreCase(kdfStr)) {
                if (parts.length != 6)
                    throw new CryptoException("Invalid v1:pbkdf2 envelope");
                int iterations = Integer.parseInt(parts[2]);
                byte[] salt = dec.decode(parts[3]);
                byte[] iv = dec.decode(parts[4]);
                byte[] ct = dec.decode(parts[5]);
                return CipherEnvelope.builder()
                        .version(version)
                        .kdf(Kdf.PBKDF2)
                        .iterations(iterations)
                        .salt(salt)
                        .iv(iv)
                        .ciphertext(ct)
                        .build();
            } else if ("raw".equalsIgnoreCase(kdfStr)) {
                if (parts.length != 4)
                    throw new CryptoException("Invalid v1:raw envelope");
                byte[] iv = dec.decode(parts[2]);
                byte[] ct = dec.decode(parts[3]);
                return CipherEnvelope.builder()
                        .version(version)
                        .kdf(Kdf.RAW)
                        .iv(iv)
                        .ciphertext(ct)
                        .build();
            } else {
                throw new CryptoException("Unknown KDF: " + kdfStr);
            }
        } catch (RuntimeException e) {
            throw new CryptoException("Failed to parse envelope", e);
        }
    }
}