package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.model.BusinessAppDTO;
import ludo.mentis.aciem.ecm.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface BusinessAppService {

	Page<BusinessAppDTO> findAll(BusinessAppDTO searchDTO, Pageable pageable);

    BusinessAppDTO get(Long id);

    Long create(BusinessAppDTO businessAppDTO);

    void update(Long id, BusinessAppDTO businessAppDTO);

    void delete(Long id);

    boolean nameExists(String name);

    ReferencedWarning getReferencedWarning(Long id);

    boolean codeExists(String code);
}
