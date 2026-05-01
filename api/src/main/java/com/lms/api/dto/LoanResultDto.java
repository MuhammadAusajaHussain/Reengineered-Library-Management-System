package com.lms.api.dto;

public class LoanResultDto {
    private String message;
    private double fineAmount;

    public LoanResultDto(String message, double fineAmount) {
        this.message = message;
        this.fineAmount = fineAmount;
    }

    public String getMessage() {
        return message;
    }

    public double getFineAmount() {
        return fineAmount;
    }
}
