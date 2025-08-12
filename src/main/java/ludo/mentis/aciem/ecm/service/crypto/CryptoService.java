package ludo.mentis.aciem.ecm.service.crypto;

import ludo.mentis.aciem.ecm.model.CipherEnvelope;

public interface CryptoService extends AutoCloseable {

    /** Encrypt UTF-8 text, returning an object envelope. */
    CipherEnvelope encryptString(String plaintext);

    /** Encrypt UTF-8 text with optional AAD, returning an object envelope. */
    CipherEnvelope encryptString(String plaintext, byte[] aad);

    /** Encrypt binary data with optional AAD, returning an object envelope. */
    CipherEnvelope encrypt(byte[] plaintext, byte[] aad);

    /** Decrypt an object envelope to bytes (validates GCM tag and optional AAD). */
    byte[] decrypt(CipherEnvelope envelope, byte[] expectedAad);

    /** Decrypt an object envelope to a UTF-8 string. */
    String decryptToString(CipherEnvelope envelope);

    /** Decrypt an object envelope (with optional AAD) to a UTF-8 string. */
    String decryptToString(CipherEnvelope envelope, byte[] expectedAad);

    @Override
    void close();
}
