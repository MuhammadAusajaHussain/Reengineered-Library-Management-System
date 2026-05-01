package com.lms.api.controller;

import com.lms.api.dto.BorrowerDto;
import com.lms.api.dto.CreateBorrowerRequest;
import com.lms.api.domain.Role;
import com.lms.api.infrastructure.session.SessionUser;
import com.lms.api.service.AuthService;
import com.lms.api.service.BorrowerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowersController {
    private final BorrowerService borrowerService;
    private final AuthService authService;

    public BorrowersController(BorrowerService borrowerService, AuthService authService) {
        this.borrowerService = borrowerService;
        this.authService = authService;
    }

    @GetMapping
    public List<BorrowerDto> getBorrowers(@RequestHeader(value = "Authorization", required = false) String authorization) {
        SessionUser sessionUser = authService.getSessionUser(authorization);
        authService.requireAnyRole(sessionUser, Role.ADMIN, Role.LIBRARIAN, Role.CLERK);
        return borrowerService.getBorrowers();
    }

    @GetMapping("/{id}")
    public BorrowerDto getBorrower(@PathVariable int id) {
        return borrowerService.getBorrower(id);
    }

    @PostMapping
    public BorrowerDto createBorrower(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody CreateBorrowerRequest request
    ) {
        SessionUser sessionUser = authService.getSessionUser(authorization);
        authService.requireAnyRole(sessionUser, Role.ADMIN, Role.LIBRARIAN, Role.CLERK);
        return borrowerService.createBorrower(request);
    }
}
