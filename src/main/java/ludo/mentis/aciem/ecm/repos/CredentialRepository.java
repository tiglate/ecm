package ludo.mentis.aciem.ecm.repos;

import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.domain.Credential;
import ludo.mentis.aciem.ecm.model.CredentialSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CredentialRepository extends JpaRepository<Credential, Long> {

    @Query("SELECT new ludo.mentis.aciem.ecm.model.CredentialSearchDTO(" +
            " d.id, " +
            " d.application.id, " +
            " d.application.name, " +
            " d.environmentId, " +
            " CASE " +
            "   WHEN d.environmentId = 1 THEN 'DEV' " +
            "   WHEN d.environmentId = 2 THEN 'QA' " +
            "   WHEN d.environmentId = 3 THEN 'UAT' " +
            "   WHEN d.environmentId = 4 THEN 'PROD' " +
            "   ELSE 'UNKNOWN' " +
            " END, " +
            " d.credentialTypeId, " +
            " CASE " +
            "   WHEN d.credentialTypeId = 1 THEN 'DATABASE' " +
            "   WHEN d.credentialTypeId = 2 THEN 'WINDOWS' " +
            "   WHEN d.credentialTypeId = 3 THEN 'LINUX' " +
            "   WHEN d.credentialTypeId = 4 THEN 'API_KEY' " +
            "   WHEN d.credentialTypeId = 5 THEN 'JWT_TOKEN' " +
            "   WHEN d.credentialTypeId = 6 THEN 'OTHER' " +
            "   ELSE 'OTHER' " +
            " END, " +
            " d.username, " +
            " d.version, " +
            " d.enabled, " +
            " d.createdAt, " +
            " d.createdBy) " +
            "FROM Credential d " +
            "WHERE (:applicationId IS NULL OR d.application.id = :applicationId) " +
            "  AND (:environmentId IS NULL OR d.environmentId = :environmentId) " +
            "  AND (:credentialTypeId IS NULL OR d.credentialTypeId = :credentialTypeId) " +
            "  AND (:enabled IS NULL OR d.enabled = :enabled) " +
            "  AND (:createdBy IS NULL OR d.createdBy LIKE %:createdBy%) " +
            "  AND (:username IS NULL OR d.username LIKE %:username%) ")
    Page<CredentialSearchDTO> findAllBySearchCriteria(
            @Param("applicationId") Long applicationId,
            @Param("environmentId") Long environmentId,
            @Param("credentialTypeId") Long credentialTypeId,
            @Param("enabled") Boolean enabled,
            @Param("createdBy") String createdBy,
            @Param("username") String username,
            Pageable pageable
    );

    Credential findFirstByApplication(BusinessApp application);
}
