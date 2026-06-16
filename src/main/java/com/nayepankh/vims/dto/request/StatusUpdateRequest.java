package com.nayepankh.vims.dto.request;

import com.nayepankh.vims.entity.VolunteerStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private VolunteerStatus status;
}
