package ludo.mentis.aciem.ecm.service;

import ludo.mentis.aciem.ecm.domain.Credential;
import ludo.mentis.aciem.ecm.model.CredentialDTO;
import ludo.mentis.aciem.ecm.model.CredentialSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CredentialService {

    Page<CredentialSearchDTO> findAll(CredentialDTO searchDTO, Pageable pageable);

    CredentialDTO get(Long id);

    Long create(CredentialDTO credentialDTO);

    void update(Long id, CredentialDTO credentialDTO);

    void delete(Long id);

    List<Credential> findHistory(Long id);
}
