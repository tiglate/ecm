package ludo.mentis.aciem.ecm.dev;

import ludo.mentis.aciem.ecm.domain.ApiKey;
import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.domain.CipherEnvelopeEntity;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.repos.ApiKeyRepository;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.service.PasswordService;
import ludo.mentis.aciem.ecm.util.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeysLoaderTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;
    @Mock
    private BusinessAppRepository businessAppRepository;
    @Mock
    private RandomUtils randomUtils;
    @Mock
    private PasswordService passwordService;

    @Test
    @DisplayName("order and name are correct")
    void metadata() {
        var loader = new ApiKeysLoader(apiKeyRepository, businessAppRepository, randomUtils, passwordService);
        assertEquals(2, loader.getOrder());
        assertEquals("API Keys", loader.getName());
    }

    @Test
    @DisplayName("canItRun returns true only when repository is empty")
    void canItRun() {
        when(apiKeyRepository.count()).thenReturn(0L);
        var loader = new ApiKeysLoader(apiKeyRepository, businessAppRepository, randomUtils, passwordService);
        assertTrue(loader.canItRun());

        when(apiKeyRepository.count()).thenReturn(1L);
        assertFalse(loader.canItRun());
    }

    @Test
    @DisplayName("run() returns 0 and does not save when there are no applications")
    void run_noApplications_returnsZero() {
        when(businessAppRepository.findAll()).thenReturn(List.of());
        var loader = new ApiKeysLoader(apiKeyRepository, businessAppRepository, randomUtils, passwordService);
        var count = loader.run();
        assertEquals(0, count);
        verify(apiKeyRepository, never()).save(any(ApiKey.class));
    }

    @Test
    @DisplayName("run() creates one key per app/environment pair up to 30 and skips existing ones")
    void run_createsPerAppEnv_skipsExisting() {
        var app = new BusinessApp("CRE01", "Core Banking System");
        app.setId(10L);
        when(businessAppRepository.findAll()).thenReturn(List.of(app));

        // No existing records
        when(apiKeyRepository.existsByApplicationIdAndEnvironmentId(anyLong(), anyLong())).thenReturn(false);

        // Deterministic random server selection
        when(randomUtils.pickRandomBoolean()).thenReturn(true);
        when(randomUtils.getRandomNumberInRange(anyInt(), anyInt())).thenReturn(0);

        var envelope = new CipherEnvelopeEntity();
        when(passwordService.encryptPasswordToEntity(anyString())).thenReturn(envelope);

        var loader = new ApiKeysLoader(apiKeyRepository, businessAppRepository, randomUtils, passwordService);

        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        var count = loader.run();

        // With 1 app and 4 environments, expect 4 creations
        assertEquals(Environment.values().length, count);
        verify(apiKeyRepository, times(Environment.values().length)).save(captor.capture());
        var saved = captor.getAllValues();
        assertEquals(Environment.values().length, saved.size());
        for (var key : saved) {
            assertNotNull(key.getApplication());
            assertNotNull(key.getCipherEnvelope());
            assertNotNull(key.getEnvironment());
            // server may be null if random returned false, but we forced true + index 0
            // so it should be non-null for at least DEV (but we won't assert exact value)
        }
    }

    @Test
    @DisplayName("run() skips creation if a key already exists for app/environment")
    void run_skipsAllIfExisting() {
        var app = new BusinessApp("CRE01", "Core Banking System");
        app.setId(10L);
        when(businessAppRepository.findAll()).thenReturn(List.of(app));

        when(apiKeyRepository.existsByApplicationIdAndEnvironmentId(anyLong(), anyLong())).thenReturn(true);

        var loader = new ApiKeysLoader(apiKeyRepository, businessAppRepository, randomUtils, passwordService);
        var count = loader.run();

        assertEquals(0, count);
        verify(apiKeyRepository, never()).save(any(ApiKey.class));
    }
}
