package com.lms.api.infrastructure.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class LoanRepository {
    private final JdbcTemplate jdbcTemplate;

    public LoanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int createLoan(int bookId, int borrowerUserId, int issuedByUserId, LocalDateTime dueDate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO loan (book_id, borrower_user_id, issued_by_user_id, issue_date, due_date, fine_paid) VALUES (?, ?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            statement.setInt(1, bookId);
            statement.setInt(2, borrowerUserId);
            statement.setInt(3, issuedByUserId);
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(5, Timestamp.valueOf(dueDate));
            statement.setBoolean(6, false);
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? 0 : key.intValue();
    }

    public LoanRow findActiveLoan(int borrowerId, int bookId) {
        List<LoanRow> rows = jdbcTemplate.query(
                "SELECT id, book_id, borrower_user_id, issue_date, due_date, fine_paid FROM loan WHERE borrower_user_id = ? AND book_id = ? AND return_date IS NULL",
                new Object[]{borrowerId, bookId},
                (rs, idx) -> new LoanRow(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("borrower_user_id"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        rs.getTimestamp("due_date").toLocalDateTime(),
                        rs.getBoolean("fine_paid")
                )
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    public int countActiveLoansForBorrower(int borrowerId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM loan WHERE borrower_user_id = ? AND return_date IS NULL",
                Integer.class,
                borrowerId
        );
        return count == null ? 0 : count.intValue();
    }

    public void closeLoan(int loanId, int returnedByUserId, boolean finePaid) {
        jdbcTemplate.update(
                "UPDATE loan SET return_date = ?, returned_by_user_id = ?, fine_paid = ? WHERE id = ?",
                Timestamp.valueOf(LocalDateTime.now()),
                returnedByUserId,
                finePaid,
                loanId
        );
    }

    public void renewLoan(int loanId, LocalDateTime newDueDate) {
        jdbcTemplate.update(
                "UPDATE loan SET due_date = ? WHERE id = ?",
                Timestamp.valueOf(newDueDate),
                loanId
        );
    }

    public void markFinePaid(int loanId) {
        jdbcTemplate.update("UPDATE loan SET fine_paid = ? WHERE id = ?", true, loanId);
    }

    public LoanRow findById(int loanId) {
        List<LoanRow> rows = jdbcTemplate.query(
                "SELECT id, book_id, borrower_user_id, issue_date, due_date, fine_paid FROM loan WHERE id = ?",
                new Object[]{loanId},
                (rs, idx) -> new LoanRow(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("borrower_user_id"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        rs.getTimestamp("due_date").toLocalDateTime(),
                        rs.getBoolean("fine_paid")
                )
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<LoanRow> listActiveLoansForBorrower(int borrowerId) {
        return jdbcTemplate.query(
                "SELECT id, book_id, borrower_user_id, issue_date, due_date, fine_paid FROM loan WHERE borrower_user_id = ? AND return_date IS NULL ORDER BY issue_date DESC",
                new Object[]{borrowerId},
                (rs, idx) -> new LoanRow(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("borrower_user_id"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        rs.getTimestamp("due_date").toLocalDateTime(),
                        rs.getBoolean("fine_paid")
                )
        );
    }

    public List<LoanRow> listAllActiveLoans() {
        return jdbcTemplate.query(
                "SELECT id, book_id, borrower_user_id, issue_date, due_date, fine_paid FROM loan WHERE return_date IS NULL ORDER BY issue_date DESC",
                (rs, idx) -> new LoanRow(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("borrower_user_id"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        rs.getTimestamp("due_date").toLocalDateTime(),
                        rs.getBoolean("fine_paid")
                )
        );
    }

    public int countAllActiveLoans() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM loan WHERE return_date IS NULL", Integer.class);
        return count == null ? 0 : count.intValue();
    }

    public int countActiveLoansForBook(int bookId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM loan WHERE book_id = ? AND return_date IS NULL",
                Integer.class,
                bookId
        );
        return count == null ? 0 : count.intValue();
    }

    public int countTotalLoans() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM loan", Integer.class);
        return count == null ? 0 : count.intValue();
    }

    public int countTotalLoansForBorrower(int borrowerId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM loan WHERE borrower_user_id = ?", Integer.class, borrowerId);
        return count == null ? 0 : count.intValue();
    }

    public int countOverdueUnpaidLoans(LocalDateTime now) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM loan WHERE return_date IS NULL AND due_date < ? AND fine_paid = ?",
                Integer.class,
                Timestamp.valueOf(now),
                false
        );
        return count == null ? 0 : count.intValue();
    }

    public int countOverdueUnpaidForBorrower(int borrowerId, LocalDateTime now) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM loan WHERE borrower_user_id = ? AND return_date IS NULL AND due_date < ? AND fine_paid = ?",
                Integer.class,
                borrowerId,
                Timestamp.valueOf(now),
                false
        );
        return count == null ? 0 : count.intValue();
    }

    public List<LoanHistoryRow> listLoanHistory(Integer borrowerId) {
        if (borrowerId == null) {
            return jdbcTemplate.query(
                    "SELECT id, book_id, borrower_user_id, issue_date, due_date, return_date, fine_paid FROM loan ORDER BY issue_date DESC",
                    (rs, idx) -> new LoanHistoryRow(
                            rs.getInt("id"),
                            rs.getInt("book_id"),
                            rs.getInt("borrower_user_id"),
                            rs.getTimestamp("issue_date").toLocalDateTime(),
                            rs.getTimestamp("due_date").toLocalDateTime(),
                            rs.getTimestamp("return_date") == null ? null : rs.getTimestamp("return_date").toLocalDateTime(),
                            rs.getBoolean("fine_paid")
                    )
            );
        }
        return jdbcTemplate.query(
                "SELECT id, book_id, borrower_user_id, issue_date, due_date, return_date, fine_paid FROM loan WHERE borrower_user_id = ? ORDER BY issue_date DESC",
                new Object[]{borrowerId},
                (rs, idx) -> new LoanHistoryRow(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("borrower_user_id"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        rs.getTimestamp("due_date").toLocalDateTime(),
                        rs.getTimestamp("return_date") == null ? null : rs.getTimestamp("return_date").toLocalDateTime(),
                        rs.getBoolean("fine_paid")
                )
        );
    }

    public static class LoanRow {
        private final int id;
        private final int bookId;
        private final int borrowerId;
        private final LocalDateTime issueDate;
        private final LocalDateTime dueDate;
        private final boolean finePaid;

        public LoanRow(int id, int bookId, int borrowerId, LocalDateTime issueDate, LocalDateTime dueDate, boolean finePaid) {
            this.id = id;
            this.bookId = bookId;
            this.borrowerId = borrowerId;
            this.issueDate = issueDate;
            this.dueDate = dueDate;
            this.finePaid = finePaid;
        }

        public int getId() { return id; }
        public int getBookId() { return bookId; }
        public int getBorrowerId() { return borrowerId; }
        public LocalDateTime getIssueDate() { return issueDate; }
        public LocalDateTime getDueDate() { return dueDate; }
        public boolean isFinePaid() { return finePaid; }
    }

    public static class LoanHistoryRow {
        private final int id;
        private final int bookId;
        private final int borrowerId;
        private final LocalDateTime issueDate;
        private final LocalDateTime dueDate;
        private final LocalDateTime returnDate;
        private final boolean finePaid;

        public LoanHistoryRow(int id, int bookId, int borrowerId, LocalDateTime issueDate, LocalDateTime dueDate, LocalDateTime returnDate, boolean finePaid) {
            this.id = id;
            this.bookId = bookId;
            this.borrowerId = borrowerId;
            this.issueDate = issueDate;
            this.dueDate = dueDate;
            this.returnDate = returnDate;
            this.finePaid = finePaid;
        }

        public int getId() { return id; }
        public int getBookId() { return bookId; }
        public int getBorrowerId() { return borrowerId; }
        public LocalDateTime getIssueDate() { return issueDate; }
        public LocalDateTime getDueDate() { return dueDate; }
        public LocalDateTime getReturnDate() { return returnDate; }
        public boolean isFinePaid() { return finePaid; }
    }
}
