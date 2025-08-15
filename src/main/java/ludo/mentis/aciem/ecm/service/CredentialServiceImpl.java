package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.domain.Credential;
import ludo.mentis.aciem.ecm.exception.IllegalOperationException;
import ludo.mentis.aciem.ecm.exception.NotFoundException;
import ludo.mentis.aciem.ecm.model.CredentialDTO;
import ludo.mentis.aciem.ecm.model.CredentialSearchDTO;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.repos.CredentialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public Page<CredentialSearchDTO> findAll(CredentialDTO searchDTO, Pageable pageable) {
        Long applicationId = searchDTO.getApplicationId();
        Long environmentId = (searchDTO.getEnvironment() != null) ? searchDTO.getEnvironment().getId() : null;
        Long credentialTypeId = (searchDTO.getCredentialType() != null) ? searchDTO.getCredentialType().getId() : null;
        Boolean enabled = searchDTO.getEnabled();
        String username = searchDTO.getUsername();
        String createdBy = searchDTO.getCreatedBy();
        return credentialRepository.findAllBySearchCriteria(
                applicationId,
                environmentId,
                credentialTypeId,
                enabled,
                createdBy,
                username,
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
        credentialDTO.setEnabled(true);
        var credential = mapToEntity(credentialDTO);
        return credentialRepository.save(credential).getId();
    }

    @Override
    public void update(final Long id, final CredentialDTO credentialDTO) {
        final var oldRecord = credentialRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        if (oldRecord.getNextCredential() != null) {
            throw new IllegalOperationException("You cannot update an old version!");
        }

        var newRecord = mapToEntity(credentialDTO);
        newRecord.setVersion(oldRecord.getVersion() + 1);
        newRecord.setUsername(oldRecord.getUsername()); // the username cannot be changed
        newRecord.setEnabled(true);

        oldRecord.setNextCredential(credentialRepository.save(newRecord));
        credentialRepository.save(oldRecord);
    }

    @Override
    public void delete(final Long id) {
        var credential = credentialRepository.findById(id).orElseThrow(NotFoundException::new);
        credential.setEnabled(false);
        credentialRepository.save(credential);
    }

    @Override
    public List<Credential> findHistory(Long id) {
        var list = new ArrayList<Credential>();
        var credential = credentialRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        while (credential != null) {
            list.add(credential);
            var previous = credentialRepository.findByNextCredential(credential);
            credential = previous.orElse(null);
        }
        list.sort((c1, c2) -> c2.getVersion().compareTo(c1.getVersion()));
        return list;
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
        credentialDTO.setIsLatest(credential.getNextCredential() == null);

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
}