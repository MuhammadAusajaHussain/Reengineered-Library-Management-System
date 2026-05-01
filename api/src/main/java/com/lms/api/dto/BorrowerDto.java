package com.lms.api.dto;

public class BorrowerDto {
    private int id;
    private String name;
    private String address;
    private int phoneNumber;
    private int borrowedBooksCount;
    private int onHoldBooksCount;

    public BorrowerDto(int id, String name, String address, int phoneNumber, int borrowedBooksCount, int onHoldBooksCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.borrowedBooksCount = borrowedBooksCount;
        this.onHoldBooksCount = onHoldBooksCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public int getBorrowedBooksCount() {
        return borrowedBooksCount;
    }

    public int getOnHoldBooksCount() {
        return onHoldBooksCount;
    }
}
