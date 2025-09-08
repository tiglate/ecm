package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.commons.web.ReferencedWarning;
import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.exception.NotFoundException;
import ludo.mentis.aciem.ecm.model.BusinessAppDTO;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.repos.CredentialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BusinessAppServiceImpl implements BusinessAppService {

    private final BusinessAppRepository businessAppRepository;
    private final CredentialRepository credentialRepository;

    public BusinessAppServiceImpl(final BusinessAppRepository businessAppRepository,
                                  final CredentialRepository credentialRepository) {
        this.businessAppRepository = businessAppRepository;
        this.credentialRepository = credentialRepository;
    }

    @Override
    public Page<BusinessAppDTO> findAll(BusinessAppDTO searchDTO, Pageable pageable) {
        return businessAppRepository.findAllBySearchCriteria(
                searchDTO.getCode(),
                searchDTO.getName(),
                pageable
        );
    }

    @Override
    public BusinessAppDTO get(final Long id) {
        return businessAppRepository.findById(id)
                .map(businessApp -> mapToDTO(businessApp, new BusinessAppDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final BusinessAppDTO businessAppDTO) {
        var businessApp = mapToEntity(businessAppDTO);
        return businessAppRepository.save(businessApp).getId();
    }

    @Override
    public void update(final Long id, final BusinessAppDTO businessAppDTO) {
        final BusinessApp businessApp = businessAppRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(businessAppDTO, businessApp);
        businessAppRepository.save(businessApp);
    }

    @Override
    public void delete(final Long id) {
        businessAppRepository.deleteById(id);
    }

    private BusinessAppDTO mapToDTO(final BusinessApp businessApp, final BusinessAppDTO businessAppDTO) {
        businessAppDTO.setId(businessApp.getId());
        businessAppDTO.setCode(businessApp.getCode());
        businessAppDTO.setName(businessApp.getName());
        businessAppDTO.setCreatedAt(businessApp.getCreatedAt());
        businessAppDTO.setUpdatedAt(businessApp.getUpdatedAt());
        return businessAppDTO;
    }

    private BusinessApp mapToEntity(final BusinessAppDTO businessAppDTO) {
        return mapToEntity(businessAppDTO, new BusinessApp());
    }

    private BusinessApp mapToEntity(final BusinessAppDTO businessAppDTO, final BusinessApp businessApp) {
        businessApp.setCode(businessAppDTO.getCode());
        businessApp.setName(businessAppDTO.getName());
        return businessApp;
    }

    @Override
    public boolean nameExists(final String name) {
        return businessAppRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean codeExists(String code) {
        return businessAppRepository.existsByCodeIgnoreCase(code);
    }

    @Override
    public ReferencedWarning getReferencedWarning(final Long id) {
        final var referencedWarning = new ReferencedWarning();
        final var businessApp = businessAppRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        var credential = credentialRepository.findFirstByApplication(businessApp);
        if (credential != null) {
            referencedWarning.setKey("businessApp.credential.referenced");
            referencedWarning.addParam(credential.getId());
            return referencedWarning;
        }
        return null;
    }

}