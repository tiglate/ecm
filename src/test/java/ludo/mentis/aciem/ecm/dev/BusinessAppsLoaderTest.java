package ludo.mentis.aciem.ecm.dev;

import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
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
class BusinessAppsLoaderTest {

    @Mock
    private BusinessAppRepository repository;

    @Test
    @DisplayName("order and name are correct")
    void metadata() {
        var loader = new BusinessAppsLoader(repository);
        assertEquals(0, loader.getOrder());
        assertEquals("Applications", loader.getName());
    }

    @Test
    @DisplayName("canItRun returns true only when repository is empty")
    void canItRun() {
        when(repository.count()).thenReturn(0L);
        var loader = new BusinessAppsLoader(repository);
        assertTrue(loader.canItRun());

        when(repository.count()).thenReturn(1L);
        assertFalse(loader.canItRun());
    }

    @Test
    @DisplayName("run() saves ten predefined applications and returns 10")
    void run_savesTenApps() {
        var loader = new BusinessAppsLoader(repository);

        ArgumentCaptor<List<BusinessApp>> captor = ArgumentCaptor.forClass(List.class);
        var count = loader.run();

        verify(repository).saveAll(captor.capture());
        var saved = captor.getValue();
        assertNotNull(saved);
        assertEquals(10, saved.size());
        assertEquals(10, count);
    }
}
