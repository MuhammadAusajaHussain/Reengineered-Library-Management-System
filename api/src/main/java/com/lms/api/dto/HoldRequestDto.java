package com.lms.api.dto;

public class HoldRequestDto {
    private final int id;
    private final int bookId;
    private final String bookTitle;
    private final String requestDate;
    private final String status;

    public HoldRequestDto(int id, int bookId, String bookTitle, String requestDate, String status) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.requestDate = requestDate;
        this.status = status;
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getRequestDate() { return requestDate; }
    public String getStatus() { return status; }
}
