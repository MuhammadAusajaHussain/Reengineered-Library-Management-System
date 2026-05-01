package com.lms.api.dto;

import javax.validation.constraints.Min;

public class FinePaymentRequest {
    @Min(value = 1, message = "loanId must be greater than 0")
    private int loanId;

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }
}
