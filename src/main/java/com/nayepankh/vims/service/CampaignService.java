package com.nayepankh.vims.service;

import com.nayepankh.vims.dto.request.CampaignStatusUpdateRequest;
import com.nayepankh.vims.dto.request.CreateCampaignRequest;
import com.nayepankh.vims.dto.request.UpdateCampaignRequest;
import com.nayepankh.vims.dto.response.CampaignDetailResponse;
import com.nayepankh.vims.dto.response.CampaignReportResponse;
import com.nayepankh.vims.dto.response.CampaignResponse;
import com.nayepankh.vims.dto.response.VolunteerResponse;
import com.nayepankh.vims.entity.Campaign;
import com.nayepankh.vims.entity.CampaignStatus;
import com.nayepankh.vims.entity.CampaignType;
import com.nayepankh.vims.entity.EnrollmentStatus;
import com.nayepankh.vims.exception.ResourceNotFoundException;
import com.nayepankh.vims.mapper.EntityMapper;
import com.nayepankh.vims.repository.CampaignRepository;
import com.nayepankh.vims.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public CampaignResponse createCampaign(CreateCampaignRequest request) {
        Campaign campaign = EntityMapper.toEntity(request);
        campaign = campaignRepository.save(campaign);
        return EntityMapper.toResponse(campaign);
    }

    public Page<CampaignResponse> getCampaigns(CampaignStatus status, CampaignType type,
                                                 boolean upcoming, Pageable pageable) {
        return campaignRepository.findWithFilters(status, type, upcoming, LocalDate.now(), pageable)
                .map(EntityMapper::toResponse);
    }

    public CampaignDetailResponse getCampaignById(Long id) {
        Campaign campaign = findCampaignOrThrow(id);
        int enrolledCount = enrollmentRepository.countActiveEnrollments(
                id, List.of(EnrollmentStatus.REGISTERED, EnrollmentStatus.ATTENDED));
        return EntityMapper.toDetailResponse(campaign, enrolledCount);
    }

    @Transactional
    public CampaignResponse updateCampaign(Long id, UpdateCampaignRequest request) {
        Campaign campaign = findCampaignOrThrow(id);
        EntityMapper.updateEntity(campaign, request);
        campaign = campaignRepository.save(campaign);
        return EntityMapper.toResponse(campaign);
    }

    @Transactional
    public CampaignResponse updateCampaignStatus(Long id, CampaignStatusUpdateRequest request) {
        Campaign campaign = findCampaignOrThrow(id);
        campaign.setStatus(request.getStatus());
        campaign = campaignRepository.save(campaign);
        return EntityMapper.toResponse(campaign);
    }

    public List<VolunteerResponse> getCampaignVolunteers(Long campaignId) {
        findCampaignOrThrow(campaignId);
        return enrollmentRepository.findByCampaignId(campaignId).stream()
                .filter(e -> e.getStatus() != EnrollmentStatus.CANCELLED)
                .map(e -> EntityMapper.toResponse(e.getVolunteer()))
                .distinct()
                .toList();
    }

    public CampaignReportResponse getCampaignReport(Long id) {
        Campaign campaign = findCampaignOrThrow(id);
        return EntityMapper.toReportResponse(campaign);
    }

    // ── Internal helpers ───────────────────────────────────────

    Campaign findCampaignOrThrow(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", id));
    }
}
