package com.lms.api.controller;

import com.lms.api.dto.BorrowerDto;
import com.lms.api.service.LegacyLibraryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowersController {
    private final LegacyLibraryService legacyLibraryService;

    public BorrowersController(LegacyLibraryService legacyLibraryService) {
        this.legacyLibraryService = legacyLibraryService;
    }

    @GetMapping("/{id}")
    public BorrowerDto getBorrower(@PathVariable int id) {
        return legacyLibraryService.getBorrower(id);
    }
}
