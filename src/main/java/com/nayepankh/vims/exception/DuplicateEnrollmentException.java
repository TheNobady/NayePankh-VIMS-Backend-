package com.nayepankh.vims.exception;

public class DuplicateEnrollmentException extends RuntimeException {

    public DuplicateEnrollmentException(String message) {
        super(message);
    }

    public DuplicateEnrollmentException(Long volunteerId, Long campaignId) {
        super("Volunteer " + volunteerId + " is already enrolled in campaign " + campaignId);
    }
}
