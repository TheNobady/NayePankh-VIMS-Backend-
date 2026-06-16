package com.nayepankh.vims.controller;

import com.nayepankh.vims.dto.request.CreateVolunteerRequest;
import com.nayepankh.vims.dto.request.StatusUpdateRequest;
import com.nayepankh.vims.dto.request.UpdateVolunteerRequest;
import com.nayepankh.vims.dto.response.EnrollmentResponse;
import com.nayepankh.vims.dto.response.VolunteerResponse;
import com.nayepankh.vims.dto.response.VolunteerSummaryResponse;
import com.nayepankh.vims.entity.VolunteerStatus;
import com.nayepankh.vims.service.VolunteerService;
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
@RequestMapping("/api/v1/volunteers")
@RequiredArgsConstructor
@Tag(name = "Volunteers", description = "Volunteer registration and management")
public class VolunteerController {

    private final VolunteerService volunteerService;

    @PostMapping
    @Operation(summary = "Register a new volunteer")
    public ResponseEntity<VolunteerResponse> createVolunteer(
            @Valid @RequestBody CreateVolunteerRequest request) {
        VolunteerResponse response = volunteerService.createVolunteer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List volunteers with optional filters")
    public ResponseEntity<Page<VolunteerResponse>> getVolunteers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) VolunteerStatus status,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(volunteerService.getVolunteers(city, skill, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a volunteer by ID")
    public ResponseEntity<VolunteerResponse> getVolunteer(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.getVolunteerById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update volunteer details")
    public ResponseEntity<VolunteerResponse> updateVolunteer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVolunteerRequest request) {
        return ResponseEntity.ok(volunteerService.updateVolunteer(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update volunteer status (activate/deactivate)")
    public ResponseEntity<VolunteerResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(volunteerService.updateVolunteerStatus(id, request));
    }

    @GetMapping("/{id}/campaigns")
    @Operation(summary = "List all campaigns a volunteer is enrolled in")
    public ResponseEntity<List<EnrollmentResponse>> getVolunteerCampaigns(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.getVolunteerCampaigns(id));
    }

    @GetMapping("/{id}/summary")
    @Operation(summary = "Get volunteer summary (total hours, campaigns joined/attended)")
    public ResponseEntity<VolunteerSummaryResponse> getVolunteerSummary(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.getVolunteerSummary(id));
    }
}
