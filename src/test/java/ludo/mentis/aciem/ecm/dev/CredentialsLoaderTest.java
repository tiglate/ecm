package ludo.mentis.aciem.ecm.dev;

import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.domain.CipherEnvelopeEntity;
import ludo.mentis.aciem.ecm.model.CredentialType;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.repos.CredentialRepository;
import ludo.mentis.aciem.ecm.service.PasswordService;
import ludo.mentis.aciem.ecm.util.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CredentialsLoaderTest {

    @Mock
    private CredentialRepository credentialRepository;
    @Mock
    private BusinessAppRepository businessAppRepository;
    @Mock
    private RandomUtils randomUtils;
    @Mock
    private PasswordService passwordService;

    @Test
    @DisplayName("order and name are correct")
    void metadata() {
        var loader = new CredentialsLoader(credentialRepository, businessAppRepository, randomUtils, passwordService);
        assertEquals(1, loader.getOrder());
        assertEquals("Credentials", loader.getName());
    }

    @Test
    @DisplayName("canItRun returns true only when repository is empty")
    void canItRun() {
        when(credentialRepository.count()).thenReturn(0L);
        var loader = new CredentialsLoader(credentialRepository, businessAppRepository, randomUtils, passwordService);
        assertTrue(loader.canItRun());

        when(credentialRepository.count()).thenReturn(1L);
        assertFalse(loader.canItRun());
    }

    @Test
    @DisplayName("run() creates 30 credentials using random and returns 30")
    void run_createsThirtyCredentials() {
        var app1 = new BusinessApp("CRE01", "Core Banking System");
        app1.setId(1L);
        var app2 = new BusinessApp("CRM02", "CRM");
        app2.setId(2L);
        when(businessAppRepository.findAll()).thenReturn(List.of(app1, app2));

        // Deterministic random for enums and booleans
        when(randomUtils.pickRandomEnumValue(Environment.class)).thenReturn(Environment.DEV);
        when(randomUtils.pickRandomEnumValue(CredentialType.class)).thenReturn(CredentialType.DATABASE);
        when(randomUtils.pickRandomBoolean()).thenReturn(true);

        when(passwordService.encryptPasswordToEntity(anyString())).thenReturn(new CipherEnvelopeEntity());

        var loader = new CredentialsLoader(credentialRepository, businessAppRepository, randomUtils, passwordService);

        var count = loader.run();
        assertEquals(30, count);
        verify(credentialRepository, times(30)).save(any());
    }
}
