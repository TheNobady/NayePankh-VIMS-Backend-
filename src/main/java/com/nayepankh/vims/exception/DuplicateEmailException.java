package com.nayepankh.vims.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("A volunteer with email '" + email + "' already exists");
    }
}
