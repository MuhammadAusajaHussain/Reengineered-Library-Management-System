package com.lms.api.dto;

public class ActiveLoanDto {
    private final int loanId;
    private final int borrowerId;
    private final int bookId;
    private final String bookTitle;
    private final String dueDate;
    private final double pendingFine;
    private final boolean finePaid;

    public ActiveLoanDto(int loanId, int borrowerId, int bookId, String bookTitle, String dueDate, double pendingFine, boolean finePaid) {
        this.loanId = loanId;
        this.borrowerId = borrowerId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.dueDate = dueDate;
        this.pendingFine = pendingFine;
        this.finePaid = finePaid;
    }

    public int getLoanId() { return loanId; }
    public int getBorrowerId() { return borrowerId; }
    public int getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getDueDate() { return dueDate; }
    public double getPendingFine() { return pendingFine; }
    public boolean isFinePaid() { return finePaid; }
}
