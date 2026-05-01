package com.lms.api.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class CreateBookRequest {
    private String isbn;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "author is required")
    private String author;

    @NotBlank(message = "subject is required")
    private String subject;

    @Min(value = 1, message = "copies must be at least 1")
    private int copies;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }
}
