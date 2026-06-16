package com.nayepankh.vims.repository;

import com.nayepankh.vims.entity.Campaign;
import com.nayepankh.vims.entity.CampaignStatus;
import com.nayepankh.vims.entity.CampaignType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    long countByStatus(CampaignStatus status);

    @Query("""
           SELECT c FROM Campaign c
           WHERE (:status IS NULL OR c.status = :status)
             AND (:type IS NULL OR c.type = :type)
             AND (:upcoming = false OR (c.eventDate >= :today AND c.status = 'UPCOMING'))
           """)
    Page<Campaign> findWithFilters(
            @Param("status") CampaignStatus status,
            @Param("type") CampaignType type,
            @Param("upcoming") boolean upcoming,
            @Param("today") LocalDate today,
            Pageable pageable
    );
}
