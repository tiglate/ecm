package ludo.mentis.aciem.ecm.model;

/**
 * Key Derivation Function
 * <p>
 * In cryptography, a key derivation function (KDF) is a cryptographic algorithm
 * that derives one or more secret keys from a secret value such as a master
 * key, a password, or a passphrase using a pseudorandom function
 */
public enum Kdf {
    PBKDF2, RAW
}