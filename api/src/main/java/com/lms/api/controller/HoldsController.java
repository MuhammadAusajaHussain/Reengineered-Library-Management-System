package com.lms.api.controller;

import com.lms.api.domain.Role;
import com.lms.api.dto.HoldRequestDto;
import com.lms.api.infrastructure.session.SessionUser;
import com.lms.api.service.AuthService;
import com.lms.api.service.HoldService;
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

    public HoldsController(HoldService holdService, AuthService authService) {
        this.holdService = holdService;
        this.authService = authService;
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
}
