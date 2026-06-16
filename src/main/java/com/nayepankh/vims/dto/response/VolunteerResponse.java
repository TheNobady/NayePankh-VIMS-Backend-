package com.nayepankh.vims.dto.response;

import com.nayepankh.vims.entity.VolunteerStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String city;
    private String skills;
    private VolunteerStatus status;
    private Instant joinedAt;
}
