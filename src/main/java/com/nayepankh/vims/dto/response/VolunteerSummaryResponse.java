package com.nayepankh.vims.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerSummaryResponse {

    private Long volunteerId;
    private String name;
    private int totalHours;
    private int campaignsJoined;
    private int campaignsAttended;
}
