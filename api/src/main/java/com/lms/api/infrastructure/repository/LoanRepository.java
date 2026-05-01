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
                "SELECT id, issue_date, due_date FROM loan WHERE borrower_user_id = ? AND book_id = ? AND return_date IS NULL",
                new Object[]{borrowerId, bookId},
                (rs, idx) -> new LoanRow(
                        rs.getInt("id"),
                        rs.getTimestamp("issue_date").toLocalDateTime(),
                        rs.getTimestamp("due_date").toLocalDateTime()
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

    public static class LoanRow {
        private final int id;
        private final LocalDateTime issueDate;
        private final LocalDateTime dueDate;

        public LoanRow(int id, LocalDateTime issueDate, LocalDateTime dueDate) {
            this.id = id;
            this.issueDate = issueDate;
            this.dueDate = dueDate;
        }

        public int getId() { return id; }
        public LocalDateTime getIssueDate() { return issueDate; }
        public LocalDateTime getDueDate() { return dueDate; }
    }
}
