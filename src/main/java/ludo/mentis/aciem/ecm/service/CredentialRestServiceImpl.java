package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.exception.NotFoundException;
import ludo.mentis.aciem.ecm.model.PasswordRequest;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.repos.CredentialRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CredentialRestServiceImpl implements CredentialRestService {

    private final CredentialRepository repository;
    private final BusinessAppRepository businessAppRepository;
    private final PasswordService passwordService;

    public CredentialRestServiceImpl(final CredentialRepository repository,
                                     final BusinessAppRepository businessAppRepository,
                                     final PasswordService passwordService) {
        this.repository = repository;
        this.businessAppRepository = businessAppRepository;
        this.passwordService = passwordService;
    }

    @Override
    public Optional<String> getPassword(final PasswordRequest passwordRequest) {
        var credential = repository.findFirstByPasswordRequest(passwordRequest);
        if (!businessAppRepository.existsByCodeIgnoreCase(passwordRequest.appCode())) {
            throw new NotFoundException("Business app not found with code: `" + passwordRequest.appCode() + "`.");
        }
        if (credential.isEmpty()) {
            return Optional.empty();
        }
        var password = this.passwordService.decryptPasswordFromEntity(credential.get().getCipherEnvelope());
        return Optional.ofNullable(password);
    }
}
