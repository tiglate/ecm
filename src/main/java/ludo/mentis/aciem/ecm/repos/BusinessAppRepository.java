package ludo.mentis.aciem.ecm.repos;

import ludo.mentis.aciem.ecm.domain.BusinessApp;
import ludo.mentis.aciem.ecm.model.BusinessAppDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BusinessAppRepository extends JpaRepository<BusinessApp, Long> {

    @Query("SELECT new ludo.mentis.aciem.ecm.model.BusinessAppDTO(d.id, d.code, d.name, d.createdAt, d.updatedAt) " +
            "FROM BusinessApp d " +
            "WHERE (:code IS NULL OR d.code LIKE %:code%) " +
            "  AND (:name IS NULL OR d.name LIKE %:name%)")
    Page<BusinessAppDTO> findAllBySearchCriteria(
            @Param("code") String code,
            @Param("name") String name,
            Pageable pageable
    );

    boolean existsByNameIgnoreCase(String name);
}
