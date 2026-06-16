package com.nayepankh.vims.exception;

public class CampaignFullException extends RuntimeException {

    public CampaignFullException(String message) {
        super(message);
    }

    public CampaignFullException(Long campaignId) {
        super("Campaign with id " + campaignId + " is at capacity");
    }
}
