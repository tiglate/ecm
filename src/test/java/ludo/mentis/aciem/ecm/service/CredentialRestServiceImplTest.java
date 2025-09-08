package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.domain.CipherEnvelopeEntity;
import ludo.mentis.aciem.ecm.domain.Credential;
import ludo.mentis.aciem.ecm.exception.NotFoundException;
import ludo.mentis.aciem.ecm.model.CredentialType;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.model.PasswordRequest;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.repos.CredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CredentialRestServiceImplTest {

    private CredentialRepository credentialRepository;
    private BusinessAppRepository businessAppRepository;
    private PasswordService passwordService;
    private CredentialRestServiceImpl service;

    @BeforeEach
    void setUp() {
        credentialRepository = mock(CredentialRepository.class);
        businessAppRepository = mock(BusinessAppRepository.class);
        passwordService = mock(PasswordService.class);
        service = new CredentialRestServiceImpl(credentialRepository, businessAppRepository, passwordService);
    }

    @Test
    void getPassword_whenBusinessAppMissing_throwsNotFound() {
        var req = new PasswordRequest("APP", Environment.DEV, CredentialType.DATABASE, "user");
        when(businessAppRepository.existsByCodeIgnoreCase("APP")).thenReturn(false);

        assertThatThrownBy(() -> service.getPassword(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("APP");
    }

    @Test
    void getPassword_whenNoCredential_returnsEmpty() {
        var req = new PasswordRequest("APP", Environment.DEV, CredentialType.DATABASE, "user");
        when(businessAppRepository.existsByCodeIgnoreCase("APP")).thenReturn(true);
        when(credentialRepository.findFirstByPasswordRequest(req)).thenReturn(Optional.empty());

        var out = service.getPassword(req);
        assertThat(out).isEmpty();
    }

    @Test
    void getPassword_whenCredentialFound_returnsDecryptedPassword() {
        var req = new PasswordRequest("APP", Environment.DEV, CredentialType.DATABASE, "user");
        when(businessAppRepository.existsByCodeIgnoreCase("APP")).thenReturn(true);
        var cred = new Credential();
        var env = new CipherEnvelopeEntity();
        env.setCiphertext(new byte[]{1});
        cred.setCipherEnvelope(env);
        when(credentialRepository.findFirstByPasswordRequest(req)).thenReturn(Optional.of(cred));
        when(passwordService.decryptPasswordFromEntity(env)).thenReturn("pass");

        var out = service.getPassword(req);
        assertThat(out).contains("pass");
    }

    @Test
    void getPassword_whenDecryptedNull_returnsEmpty() {
        var req = new PasswordRequest("APP", Environment.DEV, CredentialType.DATABASE, "user");
        when(businessAppRepository.existsByCodeIgnoreCase("APP")).thenReturn(true);
        var cred = new Credential();
        var env = new CipherEnvelopeEntity();
        cred.setCipherEnvelope(env);
        when(credentialRepository.findFirstByPasswordRequest(req)).thenReturn(Optional.of(cred));
        when(passwordService.decryptPasswordFromEntity(env)).thenReturn(null);

        var out = service.getPassword(req);
        assertThat(out).isEmpty();
    }
}
