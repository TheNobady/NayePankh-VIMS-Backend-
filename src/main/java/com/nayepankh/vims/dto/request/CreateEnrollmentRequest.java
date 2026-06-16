package com.nayepankh.vims.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEnrollmentRequest {

    @NotNull(message = "Volunteer ID is required")
    private Long volunteerId;

    @NotNull(message = "Campaign ID is required")
    private Long campaignId;
}
