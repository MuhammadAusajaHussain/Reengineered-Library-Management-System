package com.lms.api.service;

import com.lms.api.dto.BorrowerDto;
import com.lms.api.dto.CreateBorrowerRequest;
import com.lms.api.exception.BadRequestException;
import com.lms.api.exception.NotFoundException;
import com.lms.api.infrastructure.repository.LoanRepository;
import com.lms.api.infrastructure.repository.UserRepository;
import com.lms.api.util.PasswordHasher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowerService {
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    public BorrowerService(UserRepository userRepository, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    public List<BorrowerDto> getBorrowers() {
        return userRepository.getBorrowers()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BorrowerDto getBorrower(int id) {
        UserRepository.BorrowerProfileRow row = userRepository.findBorrowerById(id);
        if (row == null) {
            throw new NotFoundException("Borrower not found: " + id);
        }
        return toDto(row);
    }

    public BorrowerDto createBorrower(CreateBorrowerRequest request) {
        if (userRepository.findByUsername(request.getUsername().trim()) != null) {
            throw new BadRequestException("Username already exists");
        }
        int id = userRepository.createBorrower(
                request.getUsername().trim(),
                PasswordHasher.sha256(request.getPassword()),
                request.getFullName().trim(),
                request.getAddress().trim(),
                request.getPhoneNo().trim()
        );
        return getBorrower(id);
    }

    private BorrowerDto toDto(UserRepository.BorrowerProfileRow row) {
        int activeLoanCount = loanRepository.countActiveLoansForBorrower(row.getId());
        return new BorrowerDto(
                row.getId(),
                row.getFullName(),
                row.getAddress(),
                row.getPhoneNo(),
                activeLoanCount,
                0
        );
    }
}
