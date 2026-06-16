package com.nayepankh.vims.dto.response;

import com.nayepankh.vims.entity.CampaignStatus;
import com.nayepankh.vims.entity.CampaignType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignDetailResponse {

    private Long id;
    private String title;
    private CampaignType type;
    private String location;
    private LocalDate eventDate;
    private int capacity;
    private CampaignStatus status;
    private String description;
    private int enrolledCount;
    private int spotsRemaining;
}
