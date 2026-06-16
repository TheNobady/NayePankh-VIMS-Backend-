package com.nayepankh.vims.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignReportResponse {

    private Long campaignId;
    private String title;
    private int registeredCount;
    private int attendedCount;
    private int noShowCount;
    private int totalHours;
}
