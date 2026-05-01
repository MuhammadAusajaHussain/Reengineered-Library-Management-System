package com.lms.api.service;

import com.lms.api.dto.HoldRequestDto;
import com.lms.api.exception.BadRequestException;
import com.lms.api.exception.NotFoundException;
import com.lms.api.infrastructure.repository.BookRepository;
import com.lms.api.infrastructure.repository.HoldRepository;
import com.lms.api.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HoldService {
    private final HoldRepository holdRepository;
    private final BookService bookService;
    private final UserRepository userRepository;

    public HoldService(HoldRepository holdRepository, BookService bookService, UserRepository userRepository) {
        this.holdRepository = holdRepository;
        this.bookService = bookService;
        this.userRepository = userRepository;
    }

    public HoldRequestDto placeHold(int borrowerId, int bookId) {
        ensureBorrowerExists(borrowerId);
        BookRepository.BookRow book = bookService.getBookById(bookId);
        if (book.getAvailableCopies() > 0) {
            throw new BadRequestException("Book is available; checkout instead of placing hold");
        }
        if (holdRepository.findActiveHold(bookId, borrowerId) != null) {
            throw new BadRequestException("Active hold already exists for this borrower and book");
        }
        int holdId = holdRepository.createHold(bookId, borrowerId);
        return new HoldRequestDto(holdId, bookId, book.getTitle(), java.time.LocalDateTime.now().toString(), "ACTIVE");
    }

    public List<HoldRequestDto> getBorrowerHolds(int borrowerId) {
        ensureBorrowerExists(borrowerId);
        return holdRepository.listActiveHoldsForBorrower(borrowerId)
                .stream()
                .map(row -> new HoldRequestDto(
                        row.getId(),
                        row.getBookId(),
                        bookService.getBookById(row.getBookId()).getTitle(),
                        row.getRequestDate().toString(),
                        row.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public void cancelHold(int borrowerId, int holdId) {
        holdRepository.cancelHold(holdId, borrowerId);
    }

    public int countActiveHolds() {
        return holdRepository.countAllActiveHolds();
    }

    private void ensureBorrowerExists(int borrowerId) {
        UserRepository.UserRow user = userRepository.findById(borrowerId);
        if (user == null || !"BORROWER".equals(user.getRole().name())) {
            throw new NotFoundException("Borrower not found: " + borrowerId);
        }
    }
}
