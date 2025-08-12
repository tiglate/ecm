package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.model.CredentialDTO;
import ludo.mentis.aciem.ecm.model.CredentialSearchDTO;
import ludo.mentis.aciem.ecm.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CredentialService {

    Page<CredentialSearchDTO> findAll(CredentialDTO searchDTO, Pageable pageable);

    CredentialDTO get(Long id);

    Long create(CredentialDTO credentialDTO);

    void update(Long id, CredentialDTO credentialDTO);

    void delete(Long id);

    boolean nameExists(String name);

    ReferencedWarning getReferencedWarning(Long id);

}
