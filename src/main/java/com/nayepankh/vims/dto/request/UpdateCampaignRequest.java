package com.nayepankh.vims.dto.request;

import com.nayepankh.vims.entity.CampaignType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCampaignRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Campaign type is required")
    private CampaignType type;

    private String location;

    @NotNull(message = "Event date is required")
    private LocalDate eventDate;

    @Positive(message = "Capacity must be greater than 0")
    private int capacity;

    private String description;
}
