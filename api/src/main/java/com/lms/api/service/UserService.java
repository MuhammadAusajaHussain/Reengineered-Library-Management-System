package com.lms.api.service;

import com.lms.api.domain.Role;
import com.lms.api.dto.CreateUserRequest;
import com.lms.api.dto.UpdateUserRequest;
import com.lms.api.dto.UserDto;
import com.lms.api.exception.BadRequestException;
import com.lms.api.exception.NotFoundException;
import com.lms.api.infrastructure.repository.UserRepository;
import com.lms.api.infrastructure.repository.LoanRepository;
import com.lms.api.util.PasswordHasher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final BookService bookService;

    public UserService(UserRepository userRepository, LoanRepository loanRepository, BookService bookService) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.bookService = bookService;
    }

    public List<UserDto> listUsers() {
        return userRepository.listUsers()
                .stream()
                .map(row -> new UserDto(row.getId(), row.getUsername(), row.getFullName(), row.getRole().name(), row.isActive()))
                .collect(Collectors.toList());
    }

    public UserDto createUser(CreateUserRequest request) {
        String username = request.getUsername().trim();
        if (userRepository.findByUsername(username) != null) {
            throw new BadRequestException("Username already exists");
        }
        Role role = Role.valueOf(request.getRole());
        boolean active = request.getActive() == null || request.getActive().booleanValue();
        
        int id;
        if (role == Role.BORROWER) {
            id = userRepository.createBorrower(
                    username,
                    PasswordHasher.sha256(request.getPassword()),
                    request.getFullName().trim(),
                    request.getAddress() != null ? request.getAddress().trim() : "",
                    request.getPhoneNo() != null ? request.getPhoneNo().trim() : ""
            );
        } else {
            id = userRepository.createUser(
                    username,
                    PasswordHasher.sha256(request.getPassword()),
                    request.getFullName().trim(),
                    role,
                    active
            );
        }
        UserRepository.UserRow created = userRepository.findById(id);
        if (created == null) {
            throw new NotFoundException("User creation failed");
        }
        return new UserDto(created.getId(), created.getUsername(), created.getFullName(), created.getRole().name(), created.isActive());
    }

    public UserDto updateUser(int userId, UpdateUserRequest request) {
        UserRepository.UserRow existing = userRepository.findById(userId);
        if (existing == null) {
            throw new NotFoundException("User not found: " + userId);
        }
        Role role = request.getRole() == null ? null : Role.valueOf(request.getRole());
        String fullName = request.getFullName() == null ? null : request.getFullName().trim();

        userRepository.updateUser(userId, fullName, role, request.getActive());
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            userRepository.updatePassword(userId, PasswordHasher.sha256(request.getPassword()));
        }

        UserRepository.UserRow updated = userRepository.findById(userId);
        return new UserDto(updated.getId(), updated.getUsername(), updated.getFullName(), updated.getRole().name(), updated.isActive());
    }

    public void deleteUser(int userId) {
        UserRepository.UserRow existing = userRepository.findById(userId);
        if (existing == null) {
            throw new NotFoundException("User not found: " + userId);
        }

        // Fix: If the user is a borrower with active loans, we must "return" the books
        // to the inventory before deleting the loan records.
        List<LoanRepository.LoanRow> activeLoans = loanRepository.listActiveLoansForBorrower(userId);
        for (LoanRepository.LoanRow loan : activeLoans) {
            bookService.incrementAvailability(loan.getBookId());
        }

        userRepository.deleteUser(userId);
    }
}

