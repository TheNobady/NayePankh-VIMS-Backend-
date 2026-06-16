package com.nayepankh.vims.service;

import com.nayepankh.vims.dto.request.CreateVolunteerRequest;
import com.nayepankh.vims.dto.request.StatusUpdateRequest;
import com.nayepankh.vims.dto.request.UpdateVolunteerRequest;
import com.nayepankh.vims.dto.response.EnrollmentResponse;
import com.nayepankh.vims.dto.response.VolunteerResponse;
import com.nayepankh.vims.dto.response.VolunteerSummaryResponse;
import com.nayepankh.vims.entity.Volunteer;
import com.nayepankh.vims.entity.VolunteerStatus;
import com.nayepankh.vims.exception.DuplicateEmailException;
import com.nayepankh.vims.exception.ResourceNotFoundException;
import com.nayepankh.vims.mapper.EntityMapper;
import com.nayepankh.vims.repository.EnrollmentRepository;
import com.nayepankh.vims.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public VolunteerResponse createVolunteer(CreateVolunteerRequest request) {
        if (volunteerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Volunteer volunteer = EntityMapper.toEntity(request);
        volunteer = volunteerRepository.save(volunteer);
        return EntityMapper.toResponse(volunteer);
    }

    public Page<VolunteerResponse> getVolunteers(String city, String skill, VolunteerStatus status, Pageable pageable) {
        return volunteerRepository.findWithFilters(city, status, skill, pageable)
                .map(EntityMapper::toResponse);
    }

    public VolunteerResponse getVolunteerById(Long id) {
        Volunteer volunteer = findVolunteerOrThrow(id);
        return EntityMapper.toResponse(volunteer);
    }

    @Transactional
    public VolunteerResponse updateVolunteer(Long id, UpdateVolunteerRequest request) {
        Volunteer volunteer = findVolunteerOrThrow(id);

        // Check if email is being changed to an already-existing email
        if (!volunteer.getEmail().equalsIgnoreCase(request.getEmail())
                && volunteerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        EntityMapper.updateEntity(volunteer, request);
        volunteer = volunteerRepository.save(volunteer);
        return EntityMapper.toResponse(volunteer);
    }

    @Transactional
    public VolunteerResponse updateVolunteerStatus(Long id, StatusUpdateRequest request) {
        Volunteer volunteer = findVolunteerOrThrow(id);
        volunteer.setStatus(request.getStatus());
        volunteer = volunteerRepository.save(volunteer);
        return EntityMapper.toResponse(volunteer);
    }

    public List<EnrollmentResponse> getVolunteerCampaigns(Long volunteerId) {
        findVolunteerOrThrow(volunteerId);
        return enrollmentRepository.findByVolunteerId(volunteerId).stream()
                .map(EntityMapper::toResponse)
                .toList();
    }

    public VolunteerSummaryResponse getVolunteerSummary(Long id) {
        Volunteer volunteer = findVolunteerOrThrow(id);
        return EntityMapper.toSummaryResponse(volunteer);
    }

    // ── Internal helpers ───────────────────────────────────────

    Volunteer findVolunteerOrThrow(Long id) {
        return volunteerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer", id));
    }
}
