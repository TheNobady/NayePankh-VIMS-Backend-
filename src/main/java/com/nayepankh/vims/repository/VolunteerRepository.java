package com.nayepankh.vims.repository;

import com.nayepankh.vims.entity.Volunteer;
import com.nayepankh.vims.entity.VolunteerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    Optional<Volunteer> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByStatus(VolunteerStatus status);

    // The CAST(... AS string) calls give PostgreSQL an explicit type for the
    // nullable bind parameters. Without them, a null :city / :skill is sent as an
    // untyped NULL, which PostgreSQL infers as bytea — producing
    // "function lower(bytea) does not exist". (H2 in dev tolerates the untyped
    // null, which is why this only surfaced against PostgreSQL in production.)
    @Query("""
           SELECT v FROM Volunteer v
           WHERE (:city IS NULL OR LOWER(v.city) = LOWER(CAST(:city AS string)))
             AND (:status IS NULL OR v.status = :status)
             AND (:skill IS NULL OR LOWER(v.skills) LIKE LOWER(CONCAT('%', CAST(:skill AS string), '%')))
           """)
    Page<Volunteer> findWithFilters(
            @Param("city") String city,
            @Param("status") VolunteerStatus status,
            @Param("skill") String skill,
            Pageable pageable
    );
}
