package com.lms.api.dto;

import javax.validation.constraints.Min;

public class LoanActionRequest {
    @Min(value = 1, message = "borrowerId must be greater than 0")
    private int borrowerId;

    @Min(value = 1, message = "bookId must be greater than 0")
    private int bookId;

    public int getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
}
