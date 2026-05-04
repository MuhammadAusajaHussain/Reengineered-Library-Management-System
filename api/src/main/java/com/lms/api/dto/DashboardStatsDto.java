package com.lms.api.dto;

public class DashboardStatsDto {
    private final int totalBooks;
    private final int totalVolumes;
    private final int activeLoans;
    private final int overdueUnpaidLoans;
    private final int activeHolds;
    private final int totalBorrowers;
    private final int totalStaff;
    private final int totalLoans;

    public DashboardStatsDto(int totalBooks, int totalVolumes, int activeLoans, int overdueUnpaidLoans, int activeHolds, int totalBorrowers, int totalStaff, int totalLoans) {
        this.totalBooks = totalBooks;
        this.totalVolumes = totalVolumes;
        this.activeLoans = activeLoans;
        this.overdueUnpaidLoans = overdueUnpaidLoans;
        this.activeHolds = activeHolds;
        this.totalBorrowers = totalBorrowers;
        this.totalStaff = totalStaff;
        this.totalLoans = totalLoans;
    }

    public int getTotalBooks() { return totalBooks; }
    public int getTotalVolumes() { return totalVolumes; }
    public int getActiveLoans() { return activeLoans; }
    public int getOverdueUnpaidLoans() { return overdueUnpaidLoans; }
    public int getActiveHolds() { return activeHolds; }
    public int getTotalBorrowers() { return totalBorrowers; }
    public int getTotalStaff() { return totalStaff; }
    public int getTotalLoans() { return totalLoans; }
}
