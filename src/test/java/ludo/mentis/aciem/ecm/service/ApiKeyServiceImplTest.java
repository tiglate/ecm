package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.domain.ApiKey;
import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.domain.CipherEnvelopeEntity;
import ludo.mentis.aciem.ecm.exception.NotFoundException;
import ludo.mentis.aciem.ecm.model.ApiKeyDTO;
import ludo.mentis.aciem.ecm.model.ApiKeySearchDTO;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.repos.ApiKeyRepository;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ApiKeyServiceImplTest {

    private ApiKeyRepository apiKeyRepository;
    private BusinessAppRepository businessAppRepository;
    private PasswordService passwordService;
    private ApiKeyServiceImpl service;

    @BeforeEach
    void setup() {
        apiKeyRepository = mock(ApiKeyRepository.class);
        businessAppRepository = mock(BusinessAppRepository.class);
        passwordService = mock(PasswordService.class);
        service = new ApiKeyServiceImpl(apiKeyRepository, businessAppRepository, passwordService);
    }

    @Test
    void findAll_delegatesToRepository() {
        var search = new ApiKeyDTO();
        search.setApplicationId(10L);
        search.setEnvironment(Environment.DEV);
        search.setClientId("cli");
        search.setServer("srv");
        search.setUpdatedBy("user");
        var pageable = PageRequest.of(0, 5);

        Page<ApiKeySearchDTO> expected = new PageImpl<>(List.of());
        when(apiKeyRepository.findAllBySearchCriteria(any(), any(), any(), any(), any(), any())).thenReturn(expected);

        Page<ApiKeySearchDTO> out = service.findAll(search, pageable);

        assertThat(out).isSameAs(expected);
        verify(apiKeyRepository).findAllBySearchCriteria(eq(10L), eq(Environment.DEV.getId()), eq("user"), eq("cli"), eq("srv"), eq(pageable));
    }

    @Test
    void get_returnsMappedDTO_andDecryptsSecret() {
        ApiKey entity = new ApiKey();
        entity.setId(5L);
        BusinessApp app = new BusinessApp("CODE", "Name");
        app.setId(3L);
        entity.setApplication(app);
        entity.setClientId("client");
        entity.setServer("server");
        entity.setEnvironment(Environment.QA);
        entity.setCreatedAt(LocalDateTime.now().minusDays(1));
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy("upd");
        var envEntity = new CipherEnvelopeEntity();
        envEntity.setIv(new byte[]{1});
        envEntity.setCiphertext(new byte[]{2});
        entity.setCipherEnvelope(envEntity);

        when(apiKeyRepository.findById(5L)).thenReturn(Optional.of(entity));
        when(passwordService.decryptPasswordFromEntity(envEntity)).thenReturn("secret");

        ApiKeyDTO dto = service.get(5L);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getApplicationId()).isEqualTo(3L);
        assertThat(dto.getClientId()).isEqualTo("client");
        assertThat(dto.getServer()).isEqualTo("server");
        assertThat(dto.getEnvironment()).isEqualTo(Environment.QA);
        assertThat(dto.getSecret()).isEqualTo("secret");
        assertThat(dto.getUpdatedBy()).isEqualTo("upd");
    }

    @Test
    void get_notFound_throws() {
        when(apiKeyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_mapsAndSaves_returnsId() {
        var dto = new ApiKeyDTO();
        dto.setApplicationId(9L);
        dto.setEnvironment(Environment.PROD);
        dto.setClientId("c");
        dto.setServer("s");
        dto.setSecret("sec");

        var app = new BusinessApp("C","N");
        app.setId(9L);
        when(businessAppRepository.findById(9L)).thenReturn(Optional.of(app));
        var saved = new ApiKey();
        saved.setId(99L);
        when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(saved);

        service.create(dto);

        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        verify(apiKeyRepository).save(captor.capture());
        var ent = captor.getValue();
        assertThat(ent.getApplication().getId()).isEqualTo(9L);
        assertThat(ent.getEnvironment()).isEqualTo(Environment.PROD);
        assertThat(ent.getClientId()).isEqualTo("c");
        assertThat(ent.getServer()).isEqualTo("s");
        verify(passwordService).encryptPasswordToEntity(eq("sec"));
    }

    @Test
    void update_whenFound_mapsAndSaves() {
        var existing = new ApiKey();
        existing.setId(7L);
        when(apiKeyRepository.findById(7L)).thenReturn(Optional.of(existing));

        var app = new BusinessApp("C","N");
        app.setId(2L);
        when(businessAppRepository.findById(2L)).thenReturn(Optional.of(app));

        var dto = new ApiKeyDTO();
        dto.setApplicationId(2L);
        dto.setEnvironment(Environment.UAT);
        dto.setClientId("x");
        dto.setServer("y");
        dto.setSecret("zzz");

        service.update(7L, dto);

        verify(apiKeyRepository).save(existing);
        assertThat(existing.getEnvironment()).isEqualTo(Environment.UAT);
        assertThat(existing.getApplication().getId()).isEqualTo(2L);
        assertThat(existing.getClientId()).isEqualTo("x");
        assertThat(existing.getServer()).isEqualTo("y");
        verify(passwordService).encryptPasswordToEntity("zzz");
    }

    @Test
    void update_notFound_throws() {
        when(apiKeyRepository.findById(3L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(3L, new ApiKeyDTO())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_delegates() {
        service.delete(5L);
        verify(apiKeyRepository).deleteById(5L);
    }

    @Test
    void clientIdExists_delegates() {
        when(apiKeyRepository.existsByClientId("cid")).thenReturn(true);
        assertThat(service.clientIdExists("cid")).isTrue();
        verify(apiKeyRepository).existsByClientId("cid");
    }
}
