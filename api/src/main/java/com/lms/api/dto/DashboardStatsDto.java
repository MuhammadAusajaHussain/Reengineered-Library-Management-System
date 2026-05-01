package com.lms.api.dto;

public class DashboardStatsDto {
    private final int totalBooks;
    private final int activeLoans;
    private final int overdueUnpaidLoans;
    private final int activeHolds;
    private final int totalBorrowers;

    public DashboardStatsDto(int totalBooks, int activeLoans, int overdueUnpaidLoans, int activeHolds, int totalBorrowers) {
        this.totalBooks = totalBooks;
        this.activeLoans = activeLoans;
        this.overdueUnpaidLoans = overdueUnpaidLoans;
        this.activeHolds = activeHolds;
        this.totalBorrowers = totalBorrowers;
    }

    public int getTotalBooks() { return totalBooks; }
    public int getActiveLoans() { return activeLoans; }
    public int getOverdueUnpaidLoans() { return overdueUnpaidLoans; }
    public int getActiveHolds() { return activeHolds; }
    public int getTotalBorrowers() { return totalBorrowers; }
}
