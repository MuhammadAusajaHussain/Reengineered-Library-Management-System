package com.lms.api.controller;

import com.lms.api.dto.BookDto;
import com.lms.api.dto.SearchBooksRequest;
import com.lms.api.service.LegacyLibraryService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BooksController {
    private final LegacyLibraryService legacyLibraryService;

    public BooksController(LegacyLibraryService legacyLibraryService) {
        this.legacyLibraryService = legacyLibraryService;
    }

    @GetMapping
    public List<BookDto> getAllBooks() {
        return legacyLibraryService.getAllBooks();
    }

    @PostMapping("/search")
    public List<BookDto> searchBooks(@Valid @RequestBody SearchBooksRequest request) {
        return legacyLibraryService.searchBooks(request.getSearchBy(), request.getQuery());
    }
}
