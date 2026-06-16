package com.nayepankh.vims.service;

import com.nayepankh.vims.dto.response.DashboardResponse;
import com.nayepankh.vims.entity.*;
import com.nayepankh.vims.repository.CampaignRepository;
import com.nayepankh.vims.repository.EnrollmentRepository;
import com.nayepankh.vims.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final VolunteerRepository volunteerRepository;
    private final CampaignRepository campaignRepository;
    private final EnrollmentRepository enrollmentRepository;

    public DashboardResponse getDashboardSummary() {
        long activeVolunteers = volunteerRepository.countByStatus(VolunteerStatus.ACTIVE);
        long upcomingCampaigns = campaignRepository.countByStatus(CampaignStatus.UPCOMING);
        int totalHours = enrollmentRepository.sumAllHoursLogged();

        // Top volunteers by total hours
        List<Enrollment> allEnrollments = enrollmentRepository.findAll();
        List<DashboardResponse.TopVolunteer> topVolunteers = allEnrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ATTENDED)
                .collect(Collectors.groupingBy(
                        e -> e.getVolunteer().getName(),
                        Collectors.summingInt(Enrollment::getHoursLogged)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> DashboardResponse.TopVolunteer.builder()
                        .name(entry.getKey())
                        .totalHours(entry.getValue())
                        .build())
                .toList();

        // Average fill rate: avg(enrolledCount / capacity) across all campaigns
        List<Campaign> allCampaigns = campaignRepository.findAll();
        double averageFillRate = 0.0;
        if (!allCampaigns.isEmpty()) {
            List<EnrollmentStatus> activeStatuses = List.of(EnrollmentStatus.REGISTERED, EnrollmentStatus.ATTENDED);
            averageFillRate = allCampaigns.stream()
                    .mapToDouble(c -> {
                        int enrolled = enrollmentRepository.countActiveEnrollments(c.getId(), activeStatuses);
                        return c.getCapacity() > 0 ? (double) enrolled / c.getCapacity() : 0.0;
                    })
                    .average()
                    .orElse(0.0);

            // Round to 2 decimal places
            averageFillRate = Math.round(averageFillRate * 100.0) / 100.0;
        }

        return DashboardResponse.builder()
                .activeVolunteerCount(activeVolunteers)
                .upcomingCampaignCount(upcomingCampaigns)
                .totalHoursLogged(totalHours)
                .topVolunteers(topVolunteers)
                .averageFillRate(averageFillRate)
                .build();
    }
}
