package com.lms.api.service;

import LMS.Book;
import LMS.Borrower;
import LMS.Clerk;
import LMS.Library;
import LMS.Loan;
import LMS.Person;
import LMS.Staff;
import com.lms.api.dto.BookDto;
import com.lms.api.dto.BorrowerDto;
import com.lms.api.exception.NotFoundException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class LegacyLibraryService implements InitializingBean {
    private final Library library = Library.getInstance();

    @Override
    public void afterPropertiesSet() {
        // Preserve legacy defaults while moving UI access to web layer.
        library.setFine(20);
        library.setRequestExpiry(7);
        library.setReturnDeadline(5);
        library.setName("FAST Library");

        Connection connection = library.makeConnection();
        if (connection == null) {
            return;
        }

        try {
            library.populateLibrary(connection);
        } catch (SQLException | IOException ignored) {
            // Keep API boot resilient; frontend can still run with empty data.
        }
    }

    public List<BookDto> searchBooks(String searchBy, String query) {
        String mode = searchBy.toLowerCase(Locale.ROOT).trim();
        String normalizedQuery = query.toLowerCase(Locale.ROOT).trim();

        return library.getBooks()
                .stream()
                .filter(book -> matches(book, mode, normalizedQuery))
                .map(this::toBookDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getAllBooks() {
        return library.getBooks()
                .stream()
                .map(this::toBookDto)
                .collect(Collectors.toList());
    }

    public BorrowerDto getBorrower(int borrowerId) {
        Borrower borrower = findBorrowerById(borrowerId);
        return new BorrowerDto(
                borrower.getID(),
                borrower.getName(),
                borrower.getAddress(),
                borrower.getPhoneNumber(),
                borrower.getBorrowedBooks().size(),
                borrower.getOnHoldBooks().size()
        );
    }

    public void checkoutBook(int borrowerId, int bookId, int staffId) {
        Borrower borrower = findBorrowerById(borrowerId);
        Book book = findBookById(bookId);
        Staff staff = findStaffById(staffId);
        book.issueBook(borrower, staff);
    }

    public void checkinBook(int borrowerId, int bookId, int staffId) {
        Borrower borrower = findBorrowerById(borrowerId);
        Staff staff = findStaffById(staffId);

        Loan activeLoan = null;
        ArrayList<Loan> borrowedBooks = borrower.getBorrowedBooks();
        for (Loan loan : borrowedBooks) {
            if (loan.getBook().getID() == bookId) {
                activeLoan = loan;
                break;
            }
        }

        if (activeLoan == null) {
            throw new NotFoundException("No active loan found for borrower/book pair");
        }

        activeLoan.getBook().returnBook(borrower, activeLoan, staff);
    }

    private boolean matches(Book book, String mode, String normalizedQuery) {
        if ("title".equals(mode)) {
            return book.getTitle().toLowerCase(Locale.ROOT).contains(normalizedQuery);
        }
        if ("author".equals(mode)) {
            return book.getAuthor().toLowerCase(Locale.ROOT).contains(normalizedQuery);
        }
        if ("subject".equals(mode)) {
            return book.getSubject().toLowerCase(Locale.ROOT).contains(normalizedQuery);
        }
        return false;
    }

    private BookDto toBookDto(Book book) {
        return new BookDto(
                book.getID(),
                book.getTitle(),
                book.getAuthor(),
                book.getSubject(),
                book.getIssuedStatus()
        );
    }

    private Book findBookById(int bookId) {
        for (Book book : library.getBooks()) {
            if (book.getID() == bookId) {
                return book;
            }
        }
        throw new NotFoundException("Book not found: " + bookId);
    }

    private Borrower findBorrowerById(int borrowerId) {
        for (Person person : library.getPersons()) {
            if (person instanceof Borrower && person.getID() == borrowerId) {
                return (Borrower) person;
            }
        }
        throw new NotFoundException("Borrower not found: " + borrowerId);
    }

    private Staff findStaffById(int staffId) {
        if (library.getLibrarian() != null && library.getLibrarian().getID() == staffId) {
            return library.getLibrarian();
        }

        for (Person person : library.getPersons()) {
            if (person instanceof Clerk && person.getID() == staffId) {
                return (Staff) person;
            }
        }

        throw new NotFoundException("Staff not found: " + staffId);
    }
}
