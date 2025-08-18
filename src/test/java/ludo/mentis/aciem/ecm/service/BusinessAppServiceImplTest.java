package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.exception.NotFoundException;
import ludo.mentis.aciem.ecm.model.BusinessAppDTO;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.repos.CredentialRepository;
import ludo.mentis.aciem.ecm.util.ReferencedWarning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BusinessAppServiceImplTest {

    private BusinessAppRepository businessAppRepository;
    private CredentialRepository credentialRepository;
    private BusinessAppServiceImpl service;

    @BeforeEach
    void setUp() {
        businessAppRepository = mock(BusinessAppRepository.class);
        credentialRepository = mock(CredentialRepository.class);
        service = new BusinessAppServiceImpl(businessAppRepository, credentialRepository);
    }

    @Test
    void findAll_delegatesToRepository() {
        var search = new BusinessAppDTO();
        search.setCode("C");
        search.setName("N");
        var pageable = PageRequest.of(0, 10);
        Page<BusinessAppDTO> expected = new PageImpl<>(List.of());
        when(businessAppRepository.findAllBySearchCriteria(any(), any(), any())).thenReturn(expected);

        var out = service.findAll(search, pageable);

        assertThat(out).isSameAs(expected);
        verify(businessAppRepository).findAllBySearchCriteria(eq("C"), eq("N"), eq(pageable));
    }

    @Test
    void get_mapsEntityToDTO() {
        BusinessApp app = new BusinessApp("C","N");
        app.setId(3L);
        when(businessAppRepository.findById(3L)).thenReturn(Optional.of(app));

        var dto = service.get(3L);
        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getCode()).isEqualTo("C");
        assertThat(dto.getName()).isEqualTo("N");
    }

    @Test
    void get_notFound_throws() {
        when(businessAppRepository.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(9L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_mapsAndSaves_returnsId() {
        var dto = new BusinessAppDTO();
        dto.setCode("C");
        dto.setName("N");
        var saved = new BusinessApp("C","N");
        saved.setId(11L);
        when(businessAppRepository.save(any(BusinessApp.class))).thenReturn(saved);

        Long id = service.create(dto);
        assertThat(id).isEqualTo(11L);
        verify(businessAppRepository).save(any(BusinessApp.class));
    }

    @Test
    void update_whenFound_mapsAndSaves() {
        var existing = new BusinessApp("A","B");
        existing.setId(4L);
        when(businessAppRepository.findById(4L)).thenReturn(Optional.of(existing));

        var dto = new BusinessAppDTO();
        dto.setCode("X");
        dto.setName("Y");

        service.update(4L, dto);
        verify(businessAppRepository).save(existing);
        assertThat(existing.getCode()).isEqualTo("X");
        assertThat(existing.getName()).isEqualTo("Y");
    }

    @Test
    void update_notFound_throws() {
        when(businessAppRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(2L, new BusinessAppDTO())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_delegates() {
        service.delete(8L);
        verify(businessAppRepository).deleteById(8L);
    }

    @Test
    void exists_checks_delegate() {
        when(businessAppRepository.existsByNameIgnoreCase("nm")).thenReturn(true);
        when(businessAppRepository.existsByCodeIgnoreCase("cd")).thenReturn(false);
        assertThat(service.nameExists("nm")).isTrue();
        assertThat(service.codeExists("cd")).isFalse();
        verify(businessAppRepository).existsByNameIgnoreCase("nm");
        verify(businessAppRepository).existsByCodeIgnoreCase("cd");
    }

    @Test
    void getReferencedWarning_whenCredentialExists_returnsWarning() {
        var app = new BusinessApp("C","N");
        app.setId(5L);
        when(businessAppRepository.findById(5L)).thenReturn(Optional.of(app));
        when(credentialRepository.findFirstByApplication(app)).thenReturn(new ludo.mentis.aciem.ecm.domain.Credential() {{ setId(123L); }});

        ReferencedWarning warning = service.getReferencedWarning(5L);
        assertThat(warning).isNotNull();
        assertThat(warning.getKey()).isEqualTo("businessApp.credential.referenced");
        assertThat(warning.getParams()).containsExactly(123L);
    }

    @Test
    void getReferencedWarning_whenNoCredential_returnsNull() {
        var app = new BusinessApp("C","N");
        app.setId(6L);
        when(businessAppRepository.findById(6L)).thenReturn(Optional.of(app));
        when(credentialRepository.findFirstByApplication(app)).thenReturn(null);

        assertThat(service.getReferencedWarning(6L)).isNull();
    }

    @Test
    void getReferencedWarning_appNotFound_throws() {
        when(businessAppRepository.findById(7L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getReferencedWarning(7L)).isInstanceOf(NotFoundException.class);
    }
}
