package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.model.ApiKeyDTO;
import ludo.mentis.aciem.ecm.model.ApiKeySearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ApiKeyService {

    Page<ApiKeySearchDTO> findAll(ApiKeyDTO searchDTO, Pageable pageable);

    ApiKeyDTO get(Long id);

    Long create(ApiKeyDTO apiKeyDTO);

    void update(Long id, ApiKeyDTO apiKeyDTO);

    void delete(Long id);
}
