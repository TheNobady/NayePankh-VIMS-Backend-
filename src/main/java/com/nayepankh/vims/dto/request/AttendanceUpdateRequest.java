package com.nayepankh.vims.dto.request;

import com.nayepankh.vims.entity.EnrollmentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceUpdateRequest {

    @NotNull(message = "Status is required")
    private EnrollmentStatus status;

    @Min(value = 0, message = "Hours logged must be >= 0")
    private int hoursLogged;
}
