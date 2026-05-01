package com.lms.api.controller;

import com.lms.api.dto.LoanActionRequest;
import com.lms.api.service.LegacyLibraryService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoansController {
    private final LegacyLibraryService legacyLibraryService;

    public LoansController(LegacyLibraryService legacyLibraryService) {
        this.legacyLibraryService = legacyLibraryService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Void> checkout(@Valid @RequestBody LoanActionRequest request) {
        legacyLibraryService.checkoutBook(request.getBorrowerId(), request.getBookId(), request.getStaffId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/checkin")
    public ResponseEntity<Void> checkin(@Valid @RequestBody LoanActionRequest request) {
        legacyLibraryService.checkinBook(request.getBorrowerId(), request.getBookId(), request.getStaffId());
        return ResponseEntity.ok().build();
    }
}
