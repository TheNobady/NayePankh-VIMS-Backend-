package com.nayepankh.vims.controller;

import com.nayepankh.vims.dto.request.CampaignStatusUpdateRequest;
import com.nayepankh.vims.dto.request.CreateCampaignRequest;
import com.nayepankh.vims.dto.request.UpdateCampaignRequest;
import com.nayepankh.vims.dto.response.CampaignDetailResponse;
import com.nayepankh.vims.dto.response.CampaignReportResponse;
import com.nayepankh.vims.dto.response.CampaignResponse;
import com.nayepankh.vims.dto.response.VolunteerResponse;
import com.nayepankh.vims.entity.CampaignStatus;
import com.nayepankh.vims.entity.CampaignType;
import com.nayepankh.vims.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
@Tag(name = "Campaigns", description = "Campaign creation and management")
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    @Operation(summary = "Create a new campaign")
    public ResponseEntity<CampaignResponse> createCampaign(
            @Valid @RequestBody CreateCampaignRequest request) {
        CampaignResponse response = campaignService.createCampaign(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List campaigns with optional filters")
    public ResponseEntity<Page<CampaignResponse>> getCampaigns(
            @RequestParam(required = false) CampaignStatus status,
            @RequestParam(required = false) CampaignType type,
            @RequestParam(defaultValue = "false") boolean upcoming,
            @PageableDefault(size = 20, sort = "eventDate") Pageable pageable) {
        return ResponseEntity.ok(campaignService.getCampaigns(status, type, upcoming, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get campaign details including enrollment counts")
    public ResponseEntity<CampaignDetailResponse> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update campaign details")
    public ResponseEntity<CampaignResponse> updateCampaign(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCampaignRequest request) {
        return ResponseEntity.ok(campaignService.updateCampaign(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update campaign status")
    public ResponseEntity<CampaignResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody CampaignStatusUpdateRequest request) {
        return ResponseEntity.ok(campaignService.updateCampaignStatus(id, request));
    }

    @GetMapping("/{id}/volunteers")
    @Operation(summary = "List active volunteers enrolled in a campaign")
    public ResponseEntity<List<VolunteerResponse>> getCampaignVolunteers(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignVolunteers(id));
    }

    @GetMapping("/{id}/report")
    @Operation(summary = "Get campaign report (attendance, hours, etc.)")
    public ResponseEntity<CampaignReportResponse> getCampaignReport(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignReport(id));
    }
}
