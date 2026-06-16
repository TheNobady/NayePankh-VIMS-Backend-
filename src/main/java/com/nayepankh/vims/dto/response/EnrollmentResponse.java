package com.nayepankh.vims.dto.response;

import com.nayepankh.vims.entity.EnrollmentStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {

    private Long id;
    private Long volunteerId;
    private String volunteerName;
    private Long campaignId;
    private String campaignTitle;
    private EnrollmentStatus status;
    private int hoursLogged;
    private Instant enrolledAt;
}
