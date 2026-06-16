package com.nayepankh.vims.dto.request;

import com.nayepankh.vims.entity.CampaignStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private CampaignStatus status;
}
