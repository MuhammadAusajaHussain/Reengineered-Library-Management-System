package com.lms.api.dto;

import javax.validation.constraints.NotBlank;

public class UpdateBorrowerRequest {
    @NotBlank
    private String fullName;
    @NotBlank
    private String address;
    @NotBlank
    private String phoneNo;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
