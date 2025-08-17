package ludo.mentis.aciem.ecm.repos;

import ludo.mentis.aciem.ecm.domain.ApiKey;
import ludo.mentis.aciem.ecm.model.ApiKeySearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    @Query("SELECT new ludo.mentis.aciem.ecm.model.ApiKeySearchDTO(" +
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
            " d.server, " +
            " d.createdAt, " +
            " d.updatedAt, " +
            " d.updatedBy) " +
            "FROM ApiKey d " +
            "WHERE (:applicationId IS NULL OR d.application.id = :applicationId) " +
            "  AND (:environmentId IS NULL OR d.environmentId  = :environmentId) " +
            "  AND (:updatedBy     IS NULL OR d.updatedBy      LIKE %:updatedBy%) " +
            "  AND (:server        IS NULL OR d.server         LIKE %:server%) ")
    Page<ApiKeySearchDTO> findAllBySearchCriteria(
            @Param("applicationId") Long applicationId,
            @Param("environmentId") Long environmentId,
            @Param("updatedBy") String updatedBy,
            @Param("server") String server,
            Pageable pageable
    );

    boolean existsByApplicationIdAndEnvironmentId(Long applicationId, Long environmentId);
}
