package com.lms.api.controller;

import com.lms.api.dto.BookDto;
import com.lms.api.dto.CreateBookRequest;
import com.lms.api.dto.SearchBooksRequest;
import com.lms.api.dto.UpdateBookRequest;
import com.lms.api.domain.Role;
import com.lms.api.infrastructure.session.SessionUser;
import com.lms.api.service.AuthService;
import com.lms.api.service.BookService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BooksController {
    private final BookService bookService;
    private final AuthService authService;

    public BooksController(BookService bookService, AuthService authService) {
        this.bookService = bookService;
        this.authService = authService;
    }

    @GetMapping
    public List<BookDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping("/deduplicate")
    public void deduplicateBooks(@RequestHeader(value = "Authorization", required = false) String authorization) {
        SessionUser sessionUser = authService.getSessionUser(authorization);
        authService.requireAnyRole(sessionUser, Role.ADMIN);
        bookService.deduplicateCatalog();
    }

    @PostMapping("/search")
    public List<BookDto> searchBooks(@Valid @RequestBody SearchBooksRequest request) {
        return bookService.searchBooks(request.getSearchBy(), request.getQuery());
    }

    @PostMapping
    public BookDto createBook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody CreateBookRequest request
    ) {
        SessionUser sessionUser = authService.getSessionUser(authorization);
        authService.requireAnyRole(sessionUser, Role.ADMIN, Role.LIBRARIAN);
        return bookService.createBook(request);
    }

    @PutMapping("/{id}")
    public BookDto updateBook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable int id,
            @Valid @RequestBody UpdateBookRequest request
    ) {
        SessionUser sessionUser = authService.getSessionUser(authorization);
        authService.requireAnyRole(sessionUser, Role.ADMIN, Role.LIBRARIAN);
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable int id
    ) {
        SessionUser sessionUser = authService.getSessionUser(authorization);
        authService.requireAnyRole(sessionUser, Role.ADMIN, Role.LIBRARIAN);
        bookService.deleteBook(id);
    }
}
