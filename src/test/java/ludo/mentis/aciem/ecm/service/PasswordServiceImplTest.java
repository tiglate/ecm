package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.config.CryptoProperties;
import ludo.mentis.aciem.ecm.model.CipherEnvelope;
import ludo.mentis.aciem.ecm.model.Kdf;
import ludo.mentis.aciem.ecm.service.crypto.CryptoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PasswordServiceImplTest {

    private CryptoService cryptoService;
    private CryptoProperties cryptoProperties;
    private PasswordServiceImpl passwordService;

    @BeforeEach
    void setUp() {
        cryptoService = mock(CryptoService.class);
        cryptoProperties = new CryptoProperties();
        cryptoProperties.setAad("test-aad");
        passwordService = new PasswordServiceImpl(cryptoService, cryptoProperties);
    }

    @Test
    void encryptPassword_delegatesToCryptoServiceWithAad() {
        when(cryptoService.encryptString(eq("secret"), any(byte[].class)))
                .thenReturn(CipherEnvelope.builder()
                        .kdf(Kdf.RAW)
                        .iv(new byte[]{1})
                        .ciphertext(new byte[]{2})
                        .build());

        CipherEnvelope env = passwordService.encryptPassword("secret");

        assertThat(env).isNotNull();
        ArgumentCaptor<byte[]> aadCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(cryptoService).encryptString(eq("secret"), aadCaptor.capture());
        assertThat(new String(aadCaptor.getValue())).isEqualTo("test-aad");
    }

    @Test
    void decryptPassword_delegatesToCryptoServiceWithAad() {
        var envelope = CipherEnvelope.builder()
                .kdf(Kdf.RAW)
                .iv(new byte[]{1})
                .ciphertext(new byte[]{2})
                .build();
        when(cryptoService.decryptToString(eq(envelope), any(byte[].class))).thenReturn("plain");

        String out = passwordService.decryptPassword(envelope);

        assertThat(out).isEqualTo("plain");
        ArgumentCaptor<byte[]> aadCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(cryptoService).decryptToString(eq(envelope), aadCaptor.capture());
        assertThat(new String(aadCaptor.getValue())).isEqualTo("test-aad");
    }
}
