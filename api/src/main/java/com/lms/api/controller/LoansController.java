package com.lms.api.controller;

import com.lms.api.dto.LoanActionRequest;
import com.lms.api.dto.LoanResultDto;
import com.lms.api.domain.Role;
import com.lms.api.infrastructure.session.SessionUser;
import com.lms.api.service.AuthService;
import com.lms.api.service.LoanService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoansController {
    private final LoanService loanService;
    private final AuthService authService;

    public LoansController(LoanService loanService, AuthService authService) {
        this.loanService = loanService;
        this.authService = authService;
    }

    @PostMapping("/checkout")
    public LoanResultDto checkout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody LoanActionRequest request
    ) {
        SessionUser sessionUser = authService.getSessionUser(authorization);
        authService.requireAnyRole(sessionUser, Role.ADMIN, Role.LIBRARIAN, Role.CLERK);
        return loanService.checkoutBook(request, sessionUser.getUserId());
    }

    @PostMapping("/checkin")
    public LoanResultDto checkin(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody LoanActionRequest request
    ) {
        SessionUser sessionUser = authService.getSessionUser(authorization);
        authService.requireAnyRole(sessionUser, Role.ADMIN, Role.LIBRARIAN, Role.CLERK);
        return loanService.checkinBook(request, sessionUser.getUserId());
    }

    @PostMapping("/renew")
    public LoanResultDto renew(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody LoanActionRequest request
    ) {
        SessionUser sessionUser = authService.getSessionUser(authorization);
        authService.requireAnyRole(sessionUser, Role.ADMIN, Role.LIBRARIAN, Role.CLERK, Role.BORROWER);
        return loanService.renewBook(request);
    }
}
