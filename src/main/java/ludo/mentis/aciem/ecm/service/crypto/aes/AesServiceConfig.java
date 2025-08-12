package ludo.mentis.aciem.ecm.service.crypto.aes;

import java.util.Objects;

import javax.crypto.SecretKey;

public final class AesServiceConfig {
    // Use exactly one: passphrase OR secretKey
    final char[] passphrase; // nullable
    final SecretKey secretKey; // nullable
    final int pbkdf2Iterations;
    final int saltLengthBytes;
    final int keyLengthBits;

    private AesServiceConfig(char[] passphrase,
                             SecretKey secretKey,
                             int pbkdf2Iterations,
                             int saltLengthBytes,
                             int keyLengthBits) {
        this.passphrase = passphrase;
        this.secretKey = secretKey;
        this.pbkdf2Iterations = pbkdf2Iterations;
        this.saltLengthBytes = saltLengthBytes;
        this.keyLengthBits = keyLengthBits;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private char[] passphrase;
        private SecretKey secretKey;
        private int pbkdf2Iterations = 210_000;
        private int saltLengthBytes = 16;
        private int keyLengthBits = 256;

        public Builder passphrase(char[] passphrase) {
            this.passphrase = Objects.requireNonNull(passphrase, "passphrase");
            return this;
        }

        public Builder secretKey(SecretKey secretKey) {
            this.secretKey = Objects.requireNonNull(secretKey, "secretKey");
            return this;
        }

        public Builder pbkdf2Iterations(int iterations) {
            if (iterations < 100_000)
                throw new IllegalArgumentException("iterations too low");
            this.pbkdf2Iterations = iterations;
            return this;
        }

        public Builder saltLengthBytes(int bytes) {
            if (bytes < 16)
                throw new IllegalArgumentException("salt >= 16 bytes");
            this.saltLengthBytes = bytes;
            return this;
        }

        public Builder keyLengthBits(int bits) {
            if (bits != 128 && bits != 192 && bits != 256)
                throw new IllegalArgumentException("AES key length must be 128/192/256");
            this.keyLengthBits = bits;
            return this;
        }

        public AesServiceConfig build() {
            if ((passphrase == null) == (secretKey == null)) {
                throw new IllegalStateException("Provide exactly one: passphrase OR secretKey");
            }
            return new AesServiceConfig(passphrase, secretKey, pbkdf2Iterations, saltLengthBytes, keyLengthBits);
        }
    }
}
