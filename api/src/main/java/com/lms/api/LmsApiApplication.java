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
            // Fix Ammar Hyder role if he is CLERK but intended to be BORROWER
            jdbcTemplate.update("UPDATE app_user SET role = 'BORROWER' WHERE full_name = 'Ammar Hyder' AND role = 'CLERK'");

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
