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

    @Query("""
           SELECT v FROM Volunteer v
           WHERE (:city IS NULL OR LOWER(v.city) = LOWER(:city))
             AND (:status IS NULL OR v.status = :status)
             AND (:skill IS NULL OR LOWER(v.skills) LIKE LOWER(CONCAT('%', :skill, '%')))
           """)
    Page<Volunteer> findWithFilters(
            @Param("city") String city,
            @Param("status") VolunteerStatus status,
            @Param("skill") String skill,
            Pageable pageable
    );
}
