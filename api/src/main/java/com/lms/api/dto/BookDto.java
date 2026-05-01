package com.lms.api.dto;

public class BookDto {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private String subject;
    private boolean issued;
    private int totalCopies;
    private int availableCopies;

    public BookDto(int id, String isbn, String title, String author, String subject, boolean issued, int totalCopies, int availableCopies) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.subject = subject;
        this.issued = issued;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
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

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }
}
