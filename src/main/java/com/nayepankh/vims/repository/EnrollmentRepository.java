package com.nayepankh.vims.repository;

import com.nayepankh.vims.entity.Enrollment;
import com.nayepankh.vims.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByVolunteerId(Long volunteerId);

    List<Enrollment> findByCampaignId(Long campaignId);

    @Query("""
           SELECT COUNT(e) FROM Enrollment e
           WHERE e.campaign.id = :campaignId
             AND e.status IN (:activeStatuses)
           """)
    int countActiveEnrollments(
            @Param("campaignId") Long campaignId,
            @Param("activeStatuses") List<EnrollmentStatus> activeStatuses
    );

    @Query("""
           SELECT e FROM Enrollment e
           WHERE e.volunteer.id = :volunteerId
             AND e.campaign.id = :campaignId
             AND e.status <> 'CANCELLED'
           """)
    Optional<Enrollment> findActiveEnrollment(
            @Param("volunteerId") Long volunteerId,
            @Param("campaignId") Long campaignId
    );

    @Query("SELECT COALESCE(SUM(e.hoursLogged), 0) FROM Enrollment e")
    int sumAllHoursLogged();
}
