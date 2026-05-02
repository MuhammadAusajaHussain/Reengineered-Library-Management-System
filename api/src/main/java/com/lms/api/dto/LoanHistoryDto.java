package com.lms.api.dto;

public class LoanHistoryDto {
    private final int loanId;
    private final int borrowerId;
    private final int bookId;
    private final String bookTitle;
    private final String issueDate;
    private final String dueDate;
    private final String returnDate;
    private final boolean finePaid;

    public LoanHistoryDto(int loanId, int borrowerId, int bookId, String bookTitle, String issueDate, String dueDate, String returnDate, boolean finePaid) {
        this.loanId = loanId;
        this.borrowerId = borrowerId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.finePaid = finePaid;
    }

    public int getLoanId() { return loanId; }
    public int getBorrowerId() { return borrowerId; }
    public int getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getIssueDate() { return issueDate; }
    public String getDueDate() { return dueDate; }
    public String getReturnDate() { return returnDate; }
    public boolean isFinePaid() { return finePaid; }
}

