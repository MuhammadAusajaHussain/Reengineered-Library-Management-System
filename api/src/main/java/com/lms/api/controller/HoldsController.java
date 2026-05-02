package com.lms.api.controller;

import com.lms.api.domain.Role;
import com.lms.api.dto.HoldRequestDto;
import com.lms.api.infrastructure.session.SessionUser;
import com.lms.api.service.AuthService;
import com.lms.api.service.HoldService;
import com.lms.api.service.LoanService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/holds")
public class HoldsController {
    private final HoldService holdService;
    private final AuthService authService;
    private final LoanService loanService;

    public HoldsController(HoldService holdService, AuthService authService, LoanService loanService) {
        this.holdService = holdService;
        this.authService = authService;
        this.loanService = loanService;
    }

    @PostMapping
    public HoldRequestDto placeHold(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam int borrowerId,
            @RequestParam int bookId
    ) {
        SessionUser user = authService.getSessionUser(authorization);
        authService.requireAnyRole(user, Role.ADMIN, Role.LIBRARIAN, Role.CLERK, Role.BORROWER);
        if (user.getRole() == Role.BORROWER && user.getUserId() != borrowerId) {
            throw new com.lms.api.exception.ForbiddenException("Borrowers can only place holds for themselves");
        }
        return holdService.placeHold(borrowerId, bookId);
    }

    @GetMapping
    public List<HoldRequestDto> listBorrowerHolds(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam int borrowerId
    ) {
        SessionUser user = authService.getSessionUser(authorization);
        authService.requireAnyRole(user, Role.ADMIN, Role.LIBRARIAN, Role.CLERK, Role.BORROWER);
        if (user.getRole() == Role.BORROWER && user.getUserId() != borrowerId) {
            throw new com.lms.api.exception.ForbiddenException("Borrowers can only view their own holds");
        }
        return holdService.getBorrowerHolds(borrowerId);
    }

    @DeleteMapping("/{holdId}")
    public void cancelHold(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable int holdId,
            @RequestParam int borrowerId
    ) {
        SessionUser user = authService.getSessionUser(authorization);
        authService.requireAnyRole(user, Role.ADMIN, Role.LIBRARIAN, Role.CLERK, Role.BORROWER);
        if (user.getRole() == Role.BORROWER && user.getUserId() != borrowerId) {
            throw new com.lms.api.exception.ForbiddenException("Borrowers can only cancel their own holds");
        }
        holdService.cancelHold(borrowerId, holdId);
    }

    // Staff can fulfill a READY hold by checking out the reserved copy.
    @PostMapping("/{holdId}/checkout")
    public com.lms.api.dto.LoanResultDto checkoutReadyHold(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable int holdId
    ) {
        SessionUser user = authService.getSessionUser(authorization);
        authService.requireAnyRole(user, Role.ADMIN, Role.LIBRARIAN, Role.CLERK);

        com.lms.api.infrastructure.repository.HoldRepository.HoldRow hold = holdService.getHold(holdId);
        if (!"READY".equalsIgnoreCase(hold.getStatus())) {
            throw new com.lms.api.exception.BadRequestException("Hold is not READY");
        }

        com.lms.api.dto.LoanActionRequest req = new com.lms.api.dto.LoanActionRequest();
        req.setBorrowerId(hold.getBorrowerId());
        req.setBookId(hold.getBookId());

        com.lms.api.dto.LoanResultDto result = loanService.checkoutBook(req, user.getUserId());
        holdService.markFulfilled(holdId);
        return result;
    }
}
