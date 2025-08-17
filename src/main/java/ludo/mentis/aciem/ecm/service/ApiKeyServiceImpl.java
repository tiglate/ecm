package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.domain.ApiKey;
import ludo.mentis.aciem.ecm.exception.NotFoundException;
import ludo.mentis.aciem.ecm.model.ApiKeyDTO;
import ludo.mentis.aciem.ecm.model.ApiKeySearchDTO;
import ludo.mentis.aciem.ecm.repos.ApiKeyRepository;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final BusinessAppRepository applicationRepository;
    private final PasswordService passwordService;

    public ApiKeyServiceImpl(final ApiKeyRepository apiKeyRepository,
                                 final BusinessAppRepository applicationRepository,
                                 final PasswordService passwordService) {
        this.apiKeyRepository = apiKeyRepository;
        this.applicationRepository = applicationRepository;
        this.passwordService = passwordService;
    }

    @Override
    public Page<ApiKeySearchDTO> findAll(ApiKeyDTO searchDTO, Pageable pageable) {
        Long applicationId = searchDTO.getApplicationId();
        Long environmentId = (searchDTO.getEnvironment() != null) ? searchDTO.getEnvironment().getId() : null;
        String server = searchDTO.getServer();
        String updatedBy = searchDTO.getUpdatedBy();
        return apiKeyRepository.findAllBySearchCriteria(
                applicationId,
                environmentId,
                updatedBy,
                server,
                pageable
        );
    }

    @Override
    public ApiKeyDTO get(final Long id) {
        return apiKeyRepository.findById(id)
                .map(apiKey -> mapToDTO(apiKey, new ApiKeyDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final ApiKeyDTO apiKeyDTO) {
        var apiKey = mapToEntity(apiKeyDTO);
        return apiKeyRepository.save(apiKey).getId();
    }

    @Override
    public void update(final Long id, final ApiKeyDTO apiKeyDTO) {
        final var apiKey = apiKeyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(apiKeyDTO, apiKey);
        apiKeyRepository.save(apiKey);
    }

    @Override
    public void delete(final Long id) {
        apiKeyRepository.deleteById(id);
    }

    private ApiKeyDTO mapToDTO(final ApiKey apiKey, final ApiKeyDTO apiKeyDTO) {
        apiKeyDTO.setId(apiKey.getId());
        apiKeyDTO.setEnvironment(apiKey.getEnvironment());
        apiKeyDTO.setApplicationId(apiKey.getApplication().getId());
        apiKeyDTO.setServer(apiKey.getServer());
        apiKeyDTO.setCreatedAt(apiKey.getCreatedAt());
        apiKeyDTO.setUpdatedAt(apiKey.getUpdatedAt());
        apiKeyDTO.setUpdatedBy(apiKey.getUpdatedBy());

        var envelop = apiKey.getCipherEnvelope();
        if (envelop != null) {
            var secret = passwordService.decryptPasswordFromEntity(envelop);
            apiKeyDTO.setSecret(secret);
        }

        return apiKeyDTO;
    }

    private ApiKey mapToEntity(final ApiKeyDTO apiKeyDTO) {
        return mapToEntity(apiKeyDTO, new ApiKey());
    }

    private ApiKey mapToEntity(final ApiKeyDTO apiKeyDTO, final ApiKey apiKey) {
        apiKey.setEnvironment(apiKeyDTO.getEnvironment());
        apiKey.setServer(apiKeyDTO.getServer());
        apiKey.setCreatedAt(apiKeyDTO.getCreatedAt());
        apiKey.setUpdatedAt(apiKeyDTO.getUpdatedAt());
        apiKey.setUpdatedBy(apiKeyDTO.getUpdatedBy());

        final var application = apiKeyDTO.getApplicationId() == null
                ? null
                : applicationRepository.findById(apiKeyDTO.getApplicationId())
                .orElseThrow(() -> new NotFoundException("application not found"));
        apiKey.setApplication(application);

        if (apiKeyDTO.getSecret() != null && !apiKeyDTO.getSecret().isBlank()) {
            var envelope = passwordService.encryptPasswordToEntity(apiKeyDTO.getSecret());
            apiKey.setCipherEnvelope(envelope);
        }

        return apiKey;
    }
}