package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.domain.Credential;
import ludo.mentis.aciem.ecm.model.CredentialDTO;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.repos.CredentialRepository;
import ludo.mentis.aciem.ecm.exception.NotFoundException;
import ludo.mentis.aciem.ecm.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class CredentialServiceImpl implements CredentialService {

    private final CredentialRepository credentialRepository;
    private final BusinessAppRepository applicationRepository;
    private final PasswordService passwordService;

    public CredentialServiceImpl(final CredentialRepository credentialRepository,
                                 final BusinessAppRepository applicationRepository,
                                 final PasswordService passwordService) {
        this.credentialRepository = credentialRepository;
        this.applicationRepository = applicationRepository;
        this.passwordService = passwordService;
    }

    @Override
    public Page<CredentialDTO> findAll(CredentialDTO searchDTO, Pageable pageable) {
        return credentialRepository.findAllBySearchCriteria(
                searchDTO.getUsername(),
                pageable
        );
    }

    @Override
    public CredentialDTO get(final Long id) {
        return credentialRepository.findById(id)
                .map(credential -> mapToDTO(credential, new CredentialDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final CredentialDTO credentialDTO) {
        var credential = mapToEntity(credentialDTO);
        return credentialRepository.save(credential).getId();
    }

    @Override
    public void update(final Long id, final CredentialDTO credentialDTO) {
        final Credential credential = credentialRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(credentialDTO, credential);
        credentialRepository.save(credential);
    }

    @Override
    public void delete(final Long id) {
        credentialRepository.deleteById(id);
    }

    private CredentialDTO mapToDTO(final Credential credential, final CredentialDTO credentialDTO) {
        credentialDTO.setId(credential.getId());
        credentialDTO.setEnvironment(credential.getEnvironment());
        credentialDTO.setCredentialType(credential.getCredentialType());
        credentialDTO.setApplicationId(credential.getApplication().getId());
        credentialDTO.setUsername(credential.getUsername());
        credentialDTO.setVersion(credential.getVersion());
        credentialDTO.setEnabled(credential.getEnabled());
        credentialDTO.setUrl(credential.getUrl());
        credentialDTO.setNotes(credential.getNotes());
        credentialDTO.setCreatedBy(credential.getCreatedBy());
        credentialDTO.setCreatedAt(credential.getCreatedAt());

        var envelop = credential.getCipherEnvelope();
        if (envelop != null) {
            var password = passwordService.decryptPasswordFromEntity(envelop);
            credentialDTO.setPassword(password);
        }

        return credentialDTO;
    }

    private Credential mapToEntity(final CredentialDTO credentialDTO) {
        return mapToEntity(credentialDTO, new Credential());
    }

    private Credential mapToEntity(final CredentialDTO credentialDTO, final Credential credential) {
        credential.setEnvironment(credentialDTO.getEnvironment());
        credential.setCredentialType(credentialDTO.getCredentialType());
        credential.setUsername(credentialDTO.getUsername());
        credential.setVersion(1); //TODO: credentialDTO.getVersion());
        credential.setEnabled(credentialDTO.getEnabled());
        credential.setUrl(credentialDTO.getUrl());
        credential.setNotes(credentialDTO.getNotes());
        credential.setCreatedBy(credentialDTO.getCreatedBy());
        credential.setCreatedAt(credentialDTO.getCreatedAt());

        final var application = credentialDTO.getApplicationId() == null
                ? null
                : applicationRepository.findById(credentialDTO.getApplicationId())
                                       .orElseThrow(() -> new NotFoundException("application not found"));
        credential.setApplication(application);

        if (credentialDTO.getPassword() != null && !credentialDTO.getPassword().isBlank()) {
            var envelope = passwordService.encryptPasswordToEntity(credentialDTO.getPassword());
            credential.setCipherEnvelope(envelope);
        }

        return credential;
    }

    @Override
    public boolean nameExists(final String name) {
        return credentialRepository.existsByUsernameIgnoreCase(name);
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        /*
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Credential credential = credentialRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        var user = userRepository.findFirstByCredential(credential);
        if (user != null) {
            referencedWarning.setKey("documentType.document.documentType.referenced");
            referencedWarning.addParam(user.getId());
            return referencedWarning;
        }
         */
        return null;
    }

}