package com.nayepankh.vims.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private long activeVolunteerCount;
    private long upcomingCampaignCount;
    private int totalHoursLogged;
    private List<TopVolunteer> topVolunteers;
    private double averageFillRate;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopVolunteer {
        private String name;
        private int totalHours;
    }
}
