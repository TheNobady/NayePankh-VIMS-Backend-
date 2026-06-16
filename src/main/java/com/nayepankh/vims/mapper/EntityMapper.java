package com.nayepankh.vims.mapper;

import com.nayepankh.vims.dto.request.CreateVolunteerRequest;
import com.nayepankh.vims.dto.request.UpdateVolunteerRequest;
import com.nayepankh.vims.dto.response.*;
import com.nayepankh.vims.entity.*;

import java.util.List;

/**
 * Manual mapper for converting between entities and DTOs.
 * Keeps JPA entities from leaking across the controller boundary.
 */
public final class EntityMapper {

    private EntityMapper() {
        // utility class
    }

    // ── Volunteer ──────────────────────────────────────────────

    public static Volunteer toEntity(CreateVolunteerRequest req) {
        return Volunteer.builder()
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .city(req.getCity())
                .skills(req.getSkills())
                .build();
    }

    public static void updateEntity(Volunteer volunteer, UpdateVolunteerRequest req) {
        volunteer.setName(req.getName());
        volunteer.setEmail(req.getEmail());
        volunteer.setPhone(req.getPhone());
        volunteer.setCity(req.getCity());
        volunteer.setSkills(req.getSkills());
    }

    public static VolunteerResponse toResponse(Volunteer volunteer) {
        return VolunteerResponse.builder()
                .id(volunteer.getId())
                .name(volunteer.getName())
                .email(volunteer.getEmail())
                .phone(volunteer.getPhone())
                .city(volunteer.getCity())
                .skills(volunteer.getSkills())
                .status(volunteer.getStatus())
                .joinedAt(volunteer.getJoinedAt())
                .build();
    }

    public static VolunteerSummaryResponse toSummaryResponse(Volunteer volunteer) {
        List<Enrollment> enrollments = volunteer.getEnrollments();

        int totalHours = enrollments.stream()
                .mapToInt(Enrollment::getHoursLogged)
                .sum();

        int campaignsJoined = (int) enrollments.stream()
                .filter(e -> e.getStatus() != EnrollmentStatus.CANCELLED)
                .count();

        int campaignsAttended = (int) enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ATTENDED)
                .count();

        return VolunteerSummaryResponse.builder()
                .volunteerId(volunteer.getId())
                .name(volunteer.getName())
                .totalHours(totalHours)
                .campaignsJoined(campaignsJoined)
                .campaignsAttended(campaignsAttended)
                .build();
    }

    // ── Campaign ───────────────────────────────────────────────

    public static Campaign toEntity(com.nayepankh.vims.dto.request.CreateCampaignRequest req) {
        return Campaign.builder()
                .title(req.getTitle())
                .type(req.getType())
                .location(req.getLocation())
                .eventDate(req.getEventDate())
                .capacity(req.getCapacity())
                .description(req.getDescription())
                .build();
    }

    public static void updateEntity(Campaign campaign, com.nayepankh.vims.dto.request.UpdateCampaignRequest req) {
        campaign.setTitle(req.getTitle());
        campaign.setType(req.getType());
        campaign.setLocation(req.getLocation());
        campaign.setEventDate(req.getEventDate());
        campaign.setCapacity(req.getCapacity());
        campaign.setDescription(req.getDescription());
    }

    public static CampaignResponse toResponse(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .type(campaign.getType())
                .location(campaign.getLocation())
                .eventDate(campaign.getEventDate())
                .capacity(campaign.getCapacity())
                .status(campaign.getStatus())
                .description(campaign.getDescription())
                .build();
    }

    public static CampaignDetailResponse toDetailResponse(Campaign campaign, int enrolledCount) {
        return CampaignDetailResponse.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .type(campaign.getType())
                .location(campaign.getLocation())
                .eventDate(campaign.getEventDate())
                .capacity(campaign.getCapacity())
                .status(campaign.getStatus())
                .description(campaign.getDescription())
                .enrolledCount(enrolledCount)
                .spotsRemaining(campaign.getCapacity() - enrolledCount)
                .build();
    }

    public static CampaignReportResponse toReportResponse(Campaign campaign) {
        List<Enrollment> enrollments = campaign.getEnrollments();

        int registeredCount = (int) enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.REGISTERED)
                .count();

        int attendedCount = (int) enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ATTENDED)
                .count();

        int noShowCount = (int) enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.NO_SHOW)
                .count();

        int totalHours = enrollments.stream()
                .mapToInt(Enrollment::getHoursLogged)
                .sum();

        return CampaignReportResponse.builder()
                .campaignId(campaign.getId())
                .title(campaign.getTitle())
                .registeredCount(registeredCount)
                .attendedCount(attendedCount)
                .noShowCount(noShowCount)
                .totalHours(totalHours)
                .build();
    }

    // ── Enrollment ─────────────────────────────────────────────

    public static EnrollmentResponse toResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .volunteerId(enrollment.getVolunteer().getId())
                .volunteerName(enrollment.getVolunteer().getName())
                .campaignId(enrollment.getCampaign().getId())
                .campaignTitle(enrollment.getCampaign().getTitle())
                .status(enrollment.getStatus())
                .hoursLogged(enrollment.getHoursLogged())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }
}
