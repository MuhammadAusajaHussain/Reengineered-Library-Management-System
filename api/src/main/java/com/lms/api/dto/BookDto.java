package com.lms.api.dto;

public class BookDto {
    private int id;
    private String title;
    private String author;
    private String subject;
    private boolean issued;

    public BookDto(int id, String title, String author, String subject, boolean issued) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.subject = subject;
        this.issued = issued;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getSubject() {
        return subject;
    }

    public boolean isIssued() {
        return issued;
    }
}
