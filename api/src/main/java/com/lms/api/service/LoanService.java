package com.lms.api.service;

import com.lms.api.dto.LoanActionRequest;
import com.lms.api.dto.LoanResultDto;
import com.lms.api.exception.BadRequestException;
import com.lms.api.exception.NotFoundException;
import com.lms.api.infrastructure.repository.BookRepository;
import com.lms.api.infrastructure.repository.LoanRepository;
import com.lms.api.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class LoanService {
    private static final int LOAN_DAYS = 14;
    private static final int RENEW_DAYS = 7;
    private static final double PER_DAY_FINE = 20.0;

    private final UserRepository userRepository;
    private final BookService bookService;
    private final LoanRepository loanRepository;

    public LoanService(UserRepository userRepository, BookService bookService, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.bookService = bookService;
        this.loanRepository = loanRepository;
    }

    public LoanResultDto checkoutBook(LoanActionRequest request, int actingStaffId) {
        ensureBorrowerExists(request.getBorrowerId());
        ensureStaffExists(actingStaffId);

        BookRepository.BookRow book = bookService.getBookById(request.getBookId());
        if (book.getAvailableCopies() <= 0) {
            throw new BadRequestException("Book is currently unavailable");
        }

        loanRepository.createLoan(book.getId(), request.getBorrowerId(), actingStaffId, LocalDateTime.now().plusDays(LOAN_DAYS));
        bookService.decrementAvailability(book.getId());
        return new LoanResultDto("Book checked out successfully", 0);
    }

    public LoanResultDto checkinBook(LoanActionRequest request, int actingStaffId) {
        ensureBorrowerExists(request.getBorrowerId());
        ensureStaffExists(actingStaffId);

        LoanRepository.LoanRow loan = loanRepository.findActiveLoan(request.getBorrowerId(), request.getBookId());
        if (loan == null) {
            throw new NotFoundException("No active loan found for borrower/book");
        }

        double fine = computeFine(loan.getDueDate(), LocalDateTime.now());
        loanRepository.closeLoan(loan.getId(), actingStaffId, fine <= 0);
        bookService.incrementAvailability(request.getBookId());
        return new LoanResultDto("Book checked in successfully", fine);
    }

    public LoanResultDto renewBook(LoanActionRequest request) {
        ensureBorrowerExists(request.getBorrowerId());
        LoanRepository.LoanRow loan = loanRepository.findActiveLoan(request.getBorrowerId(), request.getBookId());
        if (loan == null) {
            throw new NotFoundException("No active loan found for borrower/book");
        }

        LocalDateTime newDueDate = loan.getDueDate().plusDays(RENEW_DAYS);
        loanRepository.renewLoan(loan.getId(), newDueDate);
        return new LoanResultDto("Loan renewed for " + RENEW_DAYS + " days", 0);
    }

    private double computeFine(LocalDateTime dueDate, LocalDateTime returnDate) {
        if (!returnDate.isAfter(dueDate)) {
            return 0;
        }
        long overdueDays = Duration.between(dueDate, returnDate).toDays();
        if (overdueDays <= 0) {
            overdueDays = 1;
        }
        return overdueDays * PER_DAY_FINE;
    }

    private void ensureBorrowerExists(int borrowerId) {
        UserRepository.UserRow user = userRepository.findById(borrowerId);
        if (user == null || !"BORROWER".equals(user.getRole().name())) {
            throw new NotFoundException("Borrower not found: " + borrowerId);
        }
    }

    private void ensureStaffExists(int userId) {
        UserRepository.UserRow user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("Staff not found: " + userId);
        }
        String role = user.getRole().name();
        if (!"ADMIN".equals(role) && !"LIBRARIAN".equals(role) && !"CLERK".equals(role)) {
            throw new BadRequestException("Provided user is not staff");
        }
    }
}
