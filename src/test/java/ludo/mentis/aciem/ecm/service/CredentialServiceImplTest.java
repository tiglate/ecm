package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.domain.CipherEnvelopeEntity;
import ludo.mentis.aciem.ecm.domain.Credential;
import ludo.mentis.aciem.ecm.exception.IllegalOperationException;
import ludo.mentis.aciem.ecm.exception.NotFoundException;
import ludo.mentis.aciem.ecm.model.CredentialDTO;
import ludo.mentis.aciem.ecm.model.CredentialSearchDTO;
import ludo.mentis.aciem.ecm.model.CredentialType;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.repos.CredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CredentialServiceImplTest {

    private CredentialRepository credentialRepository;
    private BusinessAppRepository businessAppRepository;
    private PasswordService passwordService;
    private CredentialServiceImpl service;

    @BeforeEach
    void setUp() {
        credentialRepository = mock(CredentialRepository.class);
        businessAppRepository = mock(BusinessAppRepository.class);
        passwordService = mock(PasswordService.class);
        service = new CredentialServiceImpl(credentialRepository, businessAppRepository, passwordService);
    }

    @Test
    void findAll_delegatesToRepository() {
        var search = new CredentialDTO();
        search.setApplicationId(1L);
        search.setEnvironment(Environment.DEV);
        search.setCredentialType(CredentialType.DATABASE);
        search.setEnabled(true);
        search.setCreatedBy("me");
        search.setUsername("u");
        var pageable = PageRequest.of(0, 20);

        Page<CredentialSearchDTO> expected = new PageImpl<>(List.of());
        when(credentialRepository.findAllBySearchCriteria(any(), any(), any(), any(), any(), any(), any())).thenReturn(expected);

        var out = service.findAll(search, pageable);
        assertThat(out).isSameAs(expected);
        verify(credentialRepository).findAllBySearchCriteria(1L, Environment.DEV.getId(), CredentialType.DATABASE.getId(), true, "me", "u", pageable);
    }

    @Test
    void get_mapsAndDecrypts() {
        var app = new BusinessApp("C","N");
        app.setId(2L);
        var c = new Credential();
        c.setId(9L);
        c.setApplication(app);
        c.setEnvironment(Environment.UAT);
        c.setCredentialType(CredentialType.WINDOWS);
        c.setUsername("john");
        c.setVersion(3);
        c.setEnabled(true);
        c.setUrl("url");
        c.setNotes("note");
        c.setCreatedBy("me");
        c.setCreatedAt(LocalDateTime.now());
        var env = new CipherEnvelopeEntity();
        env.setCiphertext(new byte[]{1});
        c.setCipherEnvelope(env);

        when(credentialRepository.findById(9L)).thenReturn(Optional.of(c));
        when(passwordService.decryptPasswordFromEntity(env)).thenReturn("pwd");

        var dto = service.get(9L);
        assertThat(dto.getApplicationId()).isEqualTo(2L);
        assertThat(dto.getEnvironment()).isEqualTo(Environment.UAT);
        assertThat(dto.getCredentialType()).isEqualTo(CredentialType.WINDOWS);
        assertThat(dto.getUsername()).isEqualTo("john");
        assertThat(dto.getVersion()).isEqualTo(3);
        assertThat(dto.getEnabled()).isTrue();
        assertThat(dto.getUrl()).isEqualTo("url");
        assertThat(dto.getNotes()).isEqualTo("note");
        assertThat(dto.getPassword()).isEqualTo("pwd");
        assertThat(dto.getCreatedBy()).isEqualTo("me");
        assertThat(dto.getIsLatest()).isTrue();
    }

    @Test
    void get_notFound_throws() {
        when(credentialRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_setsEnabled_andEncrypts_andReturnsId() {
        var dto = new CredentialDTO();
        dto.setApplicationId(7L);
        dto.setEnvironment(Environment.QA);
        dto.setCredentialType(CredentialType.API_KEY);
        dto.setUsername("u");
        dto.setPassword("p");
        dto.setEnabled(false); // service should override to true

        var app = new BusinessApp("C","N");
        app.setId(7L);
        when(businessAppRepository.findById(7L)).thenReturn(Optional.of(app));

        var saved = new Credential();
        saved.setId(100L);
        when(credentialRepository.save(any(Credential.class))).thenReturn(saved);

        Long id = service.create(dto);
        assertThat(id).isEqualTo(100L);
        ArgumentCaptor<Credential> cap = ArgumentCaptor.forClass(Credential.class);
        verify(credentialRepository).save(cap.capture());
        var ent = cap.getValue();
        assertThat(ent.getEnabled()).isTrue();
        assertThat(ent.getVersion()).isEqualTo(1);
        verify(passwordService).encryptPasswordToEntity("p");
    }

    @Test
    void update_whenOldHasNext_throwsIllegalOperation() {
        var old = new Credential();
        old.setId(1L);
        old.setUsername("john");
        old.setVersion(1);
        old.setNextCredential(new Credential());
        when(credentialRepository.findById(1L)).thenReturn(Optional.of(old));

        var dto = new CredentialDTO();
        assertThatThrownBy(() -> service.update(1L, dto))
                .isInstanceOf(IllegalOperationException.class);
    }

    @Test
    void update_createsNewVersion_andLinksOld() {
        var old = new Credential();
        old.setId(1L);
        old.setUsername("john");
        old.setVersion(1);
        when(credentialRepository.findById(1L)).thenReturn(Optional.of(old));

        var app = new BusinessApp("C","N");
        app.setId(3L);
        when(businessAppRepository.findById(3L)).thenReturn(Optional.of(app));

        var dto = new CredentialDTO();
        dto.setApplicationId(3L);
        dto.setEnvironment(Environment.PROD);
        dto.setCredentialType(CredentialType.LINUX);
        dto.setPassword("new");
        dto.setUsername("shouldBeIgnored");
        dto.setEnabled(false);

        when(credentialRepository.save(any(Credential.class))).thenAnswer(inv -> inv.getArgument(0));

        service.update(1L, dto);

        // First save should be new record (linked later), second save is old updated with nextCredential
        verify(credentialRepository, times(2)).save(any(Credential.class));
        assertThat(old.getNextCredential()).isNotNull();
        assertThat(old.getNextCredential().getVersion()).isEqualTo(2);
        assertThat(old.getNextCredential().getUsername()).isEqualTo("john");
        assertThat(old.getNextCredential().getEnabled()).isTrue();
        verify(passwordService).encryptPasswordToEntity("new");
    }

    @Test
    void delete_softDisables() {
        var entity = new Credential();
        entity.setId(2L);
        entity.setEnabled(true);
        when(credentialRepository.findById(2L)).thenReturn(Optional.of(entity));

        service.delete(2L);

        assertThat(entity.getEnabled()).isFalse();
        verify(credentialRepository).save(entity);
    }

    @Test
    void delete_notFound_throws() {
        when(credentialRepository.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(5L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findHistory_traversesBackwards_andSortsDescendingByVersion() {
        var c3 = new Credential(); c3.setId(3L); c3.setVersion(3);
        var c2 = new Credential(); c2.setId(2L); c2.setVersion(2);
        var c1 = new Credential(); c1.setId(1L); c1.setVersion(1);
        when(credentialRepository.findById(3L)).thenReturn(Optional.of(c3));
        when(credentialRepository.findByNextCredential(c3)).thenReturn(Optional.of(c2));
        when(credentialRepository.findByNextCredential(c2)).thenReturn(Optional.of(c1));
        when(credentialRepository.findByNextCredential(c1)).thenReturn(Optional.empty());

        var list = service.findHistory(3L);
        assertThat(list).extracting(Credential::getVersion).containsExactly(3,2,1);
    }
}
