package com.nayepankh.vims.controller;

import com.nayepankh.vims.dto.request.AttendanceUpdateRequest;
import com.nayepankh.vims.dto.request.CreateEnrollmentRequest;
import com.nayepankh.vims.dto.response.EnrollmentResponse;
import com.nayepankh.vims.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Volunteer enrollment in campaigns")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Enroll a volunteer in a campaign")
    public ResponseEntity<EnrollmentResponse> createEnrollment(
            @Valid @RequestBody CreateEnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.createEnrollment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/attendance")
    @Operation(summary = "Update enrollment attendance and hours")
    public ResponseEntity<EnrollmentResponse> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceUpdateRequest request) {
        return ResponseEntity.ok(enrollmentService.updateAttendance(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an enrollment (soft-delete)")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Long id) {
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}
