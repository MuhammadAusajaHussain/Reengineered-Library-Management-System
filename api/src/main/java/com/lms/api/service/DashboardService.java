package com.lms.api.service;

import com.lms.api.dto.DashboardStatsDto;
import com.lms.api.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final BookService bookService;
    private final LoanService loanService;
    private final HoldService holdService;
    private final UserRepository userRepository;

    public DashboardService(BookService bookService, LoanService loanService, HoldService holdService, UserRepository userRepository) {
        this.bookService = bookService;
        this.loanService = loanService;
        this.holdService = holdService;
        this.userRepository = userRepository;
    }

    public DashboardStatsDto getStats(Integer borrowerId) {
        if (borrowerId != null) {
            // Personal stats for Borrower
            return new DashboardStatsDto(
                    bookService.countBooks(),
                    loanService.countActiveLoansForBorrower(borrowerId),
                    loanService.countOverdueUnpaidForBorrower(borrowerId),
                    holdService.countActiveHoldsForBorrower(borrowerId),
                    0, // totalBorrowers (not relevant for borrower)
                    0, // totalStaff (not relevant for borrower)
                    loanService.countTotalLoansForBorrower(borrowerId)
            );
        }
        
        // General stats for Staff
        return new DashboardStatsDto(
                bookService.countTotalCopies(),
                loanService.countActiveBorrowers(), // Number of people with books
                loanService.countOverdueUnpaidLoans(),
                holdService.countActiveHolds(),
                userRepository.countBorrowers(),
                userRepository.countStaff(),
                bookService.countBooksCurrentlyOut() // Matches "not in inventory" wording and remains accurate even with holds
        );
    }
}
