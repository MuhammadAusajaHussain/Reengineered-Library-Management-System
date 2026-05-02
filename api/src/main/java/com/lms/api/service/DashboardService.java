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

    public DashboardStatsDto getStats() {
        return new DashboardStatsDto(
                bookService.countBooks(),
                loanService.countActiveLoans(),
                loanService.countOverdueUnpaidLoans(),
                holdService.countActiveHolds(),
                userRepository.countBorrowers(),
                userRepository.countStaff()
        );
    }
}
