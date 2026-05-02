package com.lms.api.service;

import com.lms.api.dto.BookDto;
import com.lms.api.dto.CreateBookRequest;
import com.lms.api.dto.UpdateBookRequest;
import com.lms.api.exception.BadRequestException;
import com.lms.api.exception.NotFoundException;
import com.lms.api.infrastructure.repository.BookRepository;
import com.lms.api.infrastructure.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public BookService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
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
        String isbn = request.getIsbn();
        String title = request.getTitle().trim();
        String author = request.getAuthor().trim();

        // Check if book already exists by ISBN or Title/Author
        BookRepository.BookRow existing = bookRepository.findByIsbn(isbn);
        if (existing == null) {
            existing = bookRepository.findByTitleAndAuthor(title, author);
        }

        if (existing != null) {
            // Update existing book by adding new copies
            int newTotal = existing.getTotalCopies() + request.getCopies();
            int newAvailable = existing.getAvailableCopies() + request.getCopies();
            bookRepository.update(
                    existing.getId(),
                    existing.getIsbn(),
                    existing.getTitle(),
                    existing.getAuthor(),
                    existing.getSubject(),
                    newTotal,
                    newAvailable
            );
            return toDto(bookRepository.findById(existing.getId()));
        }

        int id = bookRepository.create(
                isbn,
                title,
                author,
                request.getSubject().trim(),
                request.getCopies()
        );
        BookRepository.BookRow row = bookRepository.findById(id);
        if (row == null) {
            throw new NotFoundException("Book creation failed");
        }
        return toDto(row);
    }

    public BookDto updateBook(int bookId, UpdateBookRequest request) {
        BookRepository.BookRow existing = bookRepository.findById(bookId);
        if (existing == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }

        int newTotal = request.getTotalCopies();
        int currentlyIssued = existing.getTotalCopies() - existing.getAvailableCopies();
        if (newTotal < currentlyIssued) {
            throw new BadRequestException("totalCopies cannot be less than currently issued copies (" + currentlyIssued + ")");
        }
        int newAvailable = newTotal - currentlyIssued;

        bookRepository.update(
                bookId,
                request.getIsbn(),
                request.getTitle().trim(),
                request.getAuthor().trim(),
                request.getSubject().trim(),
                newTotal,
                newAvailable
        );
        BookRepository.BookRow updated = bookRepository.findById(bookId);
        return toDto(updated);
    }

    public void deleteBook(int bookId) {
        if (bookRepository.findById(bookId) == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        bookRepository.delete(bookId);
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

    /**
     * Repairs the available_copies for a book by matching it to (total_copies - active_loans).
     * This fixes data inconsistencies caused by deleting users who had active loans.
     */
    public void repairAvailability(int bookId) {
        BookRepository.BookRow book = bookRepository.findById(bookId);
        if (book == null) return;
        
        int activeLoansCount = loanRepository.countActiveLoansForBook(bookId);
        int correctAvailable = book.getTotalCopies() - activeLoansCount;
        
        if (book.getAvailableCopies() != correctAvailable) {
            bookRepository.update(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getSubject(),
                book.getTotalCopies(),
                correctAvailable
            );
        }
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
