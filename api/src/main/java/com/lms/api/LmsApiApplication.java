package com.lms.api;

import com.lms.api.infrastructure.repository.BookRepository;
import com.lms.api.infrastructure.repository.UserRepository;
import com.lms.api.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LmsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LmsApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner repairData(BookService bookService, BookRepository bookRepository, UserRepository userRepository, org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        return args -> {
            // Bootstrap books if none exist
            if (bookRepository.countBooks() == 0) {
                bookService.createBook(new com.lms.api.dto.CreateBookRequest("9780134685991", "Effective Java", "Joshua Bloch", "Programming", 3));
                bookService.createBook(new com.lms.api.dto.CreateBookRequest("9780132350884", "Clean Code", "Robert C. Martin", "Programming", 2));
                bookService.createBook(new com.lms.api.dto.CreateBookRequest("9781492056270", "Designing Data-Intensive Applications", "Martin Kleppmann", "Systems", 1));
            }

            // Deduplicate catalog
            bookService.deduplicateCatalog();

            // Repair book availability
            bookRepository.getAll().forEach(book -> {
                bookService.repairAvailability(book.getId());
            });

            // Repair missing borrower profiles
            userRepository.listUsers().forEach(user -> {
                if (user.getRole() == com.lms.api.domain.Role.BORROWER) {
                    userRepository.ensureBorrowerProfile(user.getId());
                }
            });
        };
    }
}
