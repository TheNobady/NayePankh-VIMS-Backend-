package com.nayepankh.vims.service;

import com.nayepankh.vims.dto.request.AttendanceUpdateRequest;
import com.nayepankh.vims.dto.request.CreateEnrollmentRequest;
import com.nayepankh.vims.dto.response.EnrollmentResponse;
import com.nayepankh.vims.entity.*;
import com.nayepankh.vims.exception.*;
import com.nayepankh.vims.mapper.EntityMapper;
import com.nayepankh.vims.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final VolunteerService volunteerService;
    private final CampaignService campaignService;

    /**
     * Enroll a volunteer in a campaign.
     *
     * Business rules enforced:
     * 1. Volunteer must exist and be ACTIVE
     * 2. Campaign must exist and be UPCOMING or ACTIVE
     * 3. No duplicate non-cancelled enrollment for the same (volunteer, campaign)
     * 4. Campaign must not be at capacity
     */
    @Transactional
    public EnrollmentResponse createEnrollment(CreateEnrollmentRequest request) {
        Volunteer volunteer = volunteerService.findVolunteerOrThrow(request.getVolunteerId());
        Campaign campaign = campaignService.findCampaignOrThrow(request.getCampaignId());

        // Rule 3: Volunteer must be ACTIVE
        if (volunteer.getStatus() == VolunteerStatus.INACTIVE) {
            throw new InvalidEnrollmentException("Volunteer is INACTIVE and cannot enroll");
        }

        // Rule 3: Campaign must be UPCOMING or ACTIVE
        if (campaign.getStatus() != CampaignStatus.UPCOMING && campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new InvalidEnrollmentException(
                    "Cannot enroll in a campaign with status: " + campaign.getStatus());
        }

        // Rule 2: No duplicate enrollment
        enrollmentRepository.findActiveEnrollment(volunteer.getId(), campaign.getId())
                .ifPresent(existing -> {
                    throw new DuplicateEnrollmentException(volunteer.getId(), campaign.getId());
                });

        // Rule 1: Check capacity
        List<EnrollmentStatus> activeStatuses = List.of(EnrollmentStatus.REGISTERED, EnrollmentStatus.ATTENDED);
        int currentCount = enrollmentRepository.countActiveEnrollments(campaign.getId(), activeStatuses);
        if (currentCount >= campaign.getCapacity()) {
            throw new CampaignFullException(campaign.getId());
        }

        Enrollment enrollment = Enrollment.builder()
                .volunteer(volunteer)
                .campaign(campaign)
                .build();

        enrollment = enrollmentRepository.save(enrollment);
        return EntityMapper.toResponse(enrollment);
    }

    /**
     * Update attendance status and hours logged for an enrollment.
     *
     * Business rule: hoursLogged can only be set when status is ATTENDED.
     */
    @Transactional
    public EnrollmentResponse updateAttendance(Long enrollmentId, AttendanceUpdateRequest request) {
        Enrollment enrollment = findEnrollmentOrThrow(enrollmentId);

        // Rule 5: hours can only be logged when ATTENDED
        if (request.getHoursLogged() > 0 && request.getStatus() != EnrollmentStatus.ATTENDED) {
            throw new InvalidEnrollmentException(
                    "Hours can only be logged when enrollment status is ATTENDED");
        }

        enrollment.setStatus(request.getStatus());
        enrollment.setHoursLogged(request.getHoursLogged());
        enrollment = enrollmentRepository.save(enrollment);
        return EntityMapper.toResponse(enrollment);
    }

    /**
     * Cancel an enrollment (soft-delete: sets status to CANCELLED).
     */
    @Transactional
    public void cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = findEnrollmentOrThrow(enrollmentId);
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
    }

    // ── Internal helpers ───────────────────────────────────────

    private Enrollment findEnrollmentOrThrow(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", id));
    }
}
