package com.lms.api.domain;

public enum Role {
    ADMIN,
    LIBRARIAN,
    CLERK,
    BORROWER;

    public boolean canManageBooks() {
        return this == ADMIN || this == LIBRARIAN;
    }

    public boolean canManageLoans() {
        return this == ADMIN || this == LIBRARIAN || this == CLERK;
    }

    public boolean canRegisterBorrower() {
        return this == ADMIN || this == LIBRARIAN || this == CLERK;
    }
}
