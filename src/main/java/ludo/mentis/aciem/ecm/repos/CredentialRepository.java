package ludo.mentis.aciem.ecm.repos;

import ludo.mentis.aciem.ecm.domain.Credential;
import ludo.mentis.aciem.ecm.model.CredentialDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CredentialRepository extends JpaRepository<Credential, Long> {

    @Query("SELECT new ludo.mentis.aciem.ecm.model.CredentialDTO(d.id, d.username, d.createdAt) " +
            "FROM Credential d " +
            "WHERE (:username IS NULL OR d.username LIKE %:username%)")
    Page<CredentialDTO> findAllBySearchCriteria(
            @Param("username") String username,
            Pageable pageable
    );

    boolean existsByUsernameIgnoreCase(String username);
}
