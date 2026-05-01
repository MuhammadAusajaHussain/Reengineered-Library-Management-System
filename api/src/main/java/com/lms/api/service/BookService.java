package com.lms.api.service;

import com.lms.api.dto.BookDto;
import com.lms.api.dto.CreateBookRequest;
import com.lms.api.exception.BadRequestException;
import com.lms.api.exception.NotFoundException;
import com.lms.api.infrastructure.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDto> getAllBooks() {
        return bookRepository.getAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<BookDto> searchBooks(String searchBy, String query) {
        String normalizedSearchBy = searchBy == null ? "" : searchBy.trim().toLowerCase();
        if (!"title".equals(normalizedSearchBy) && !"author".equals(normalizedSearchBy) && !"subject".equals(normalizedSearchBy)) {
            throw new BadRequestException("searchBy must be title, author, or subject");
        }
        return bookRepository.search(normalizedSearchBy, query == null ? "" : query.trim())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BookDto createBook(CreateBookRequest request) {
        int id = bookRepository.create(
                request.getIsbn(),
                request.getTitle().trim(),
                request.getAuthor().trim(),
                request.getSubject().trim(),
                request.getCopies()
        );
        BookRepository.BookRow row = bookRepository.findById(id);
        if (row == null) {
            throw new NotFoundException("Book creation failed");
        }
        return toDto(row);
    }

    public BookRepository.BookRow getBookById(int bookId) {
        BookRepository.BookRow row = bookRepository.findById(bookId);
        if (row == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        return row;
    }

    public void decrementAvailability(int bookId) {
        bookRepository.changeAvailability(bookId, -1);
    }

    public void incrementAvailability(int bookId) {
        bookRepository.changeAvailability(bookId, 1);
    }

    public int countBooks() {
        return bookRepository.countBooks();
    }

    private BookDto toDto(BookRepository.BookRow row) {
        return new BookDto(
                row.getId(),
                row.getIsbn(),
                row.getTitle(),
                row.getAuthor(),
                row.getSubject(),
                row.getAvailableCopies() <= 0,
                row.getTotalCopies(),
                row.getAvailableCopies()
        );
    }
}
