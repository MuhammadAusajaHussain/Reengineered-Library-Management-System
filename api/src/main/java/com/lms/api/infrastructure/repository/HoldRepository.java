package com.lms.api.infrastructure.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class HoldRepository {
    private final JdbcTemplate jdbcTemplate;

    public HoldRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int createHold(int bookId, int borrowerId) {
        jdbcTemplate.update(
                "INSERT INTO hold_request (book_id, borrower_user_id, request_date, status) VALUES (?, ?, ?, ?)",
                bookId,
                borrowerId,
                Timestamp.valueOf(LocalDateTime.now()),
                "ACTIVE"
        );
        Integer id = jdbcTemplate.queryForObject("VALUES IDENTITY_VAL_LOCAL()", Integer.class);
        return id == null ? 0 : id.intValue();
    }

    public HoldRow findActiveHold(int bookId, int borrowerId) {
        List<HoldRow> rows = jdbcTemplate.query(
                "SELECT id, book_id, borrower_user_id, request_date, status FROM hold_request WHERE book_id = ? AND borrower_user_id = ? AND status = 'ACTIVE'",
                new Object[]{bookId, borrowerId},
                (rs, idx) -> new HoldRow(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("borrower_user_id"),
                        rs.getTimestamp("request_date").toLocalDateTime(),
                        rs.getString("status")
                )
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    public HoldRow findById(int holdId) {
        List<HoldRow> rows = jdbcTemplate.query(
                "SELECT id, book_id, borrower_user_id, request_date, status FROM hold_request WHERE id = ?",
                new Object[]{holdId},
                (rs, idx) -> new HoldRow(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("borrower_user_id"),
                        rs.getTimestamp("request_date").toLocalDateTime(),
                        rs.getString("status")
                )
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    public HoldRow findOldestActiveHoldForBook(int bookId) {
        List<HoldRow> rows = jdbcTemplate.query(
                "SELECT id, book_id, borrower_user_id, request_date, status FROM hold_request WHERE book_id = ? AND status = 'ACTIVE' ORDER BY request_date ASC",
                new Object[]{bookId},
                (rs, idx) -> new HoldRow(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("borrower_user_id"),
                        rs.getTimestamp("request_date").toLocalDateTime(),
                        rs.getString("status")
                )
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<HoldRow> listHoldsForBorrower(int borrowerId) {
        return jdbcTemplate.query(
                "SELECT id, book_id, borrower_user_id, request_date, status FROM hold_request WHERE borrower_user_id = ? ORDER BY request_date DESC",
                new Object[]{borrowerId},
                (rs, idx) -> new HoldRow(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("borrower_user_id"),
                        rs.getTimestamp("request_date").toLocalDateTime(),
                        rs.getString("status")
                )
        );
    }

    public void updateStatus(int holdId, String status) {
        jdbcTemplate.update("UPDATE hold_request SET status = ? WHERE id = ?", status, holdId);
    }

    public List<HoldRow> listActiveHoldsForBorrower(int borrowerId) {
        return jdbcTemplate.query(
                "SELECT id, book_id, borrower_user_id, request_date, status FROM hold_request WHERE borrower_user_id = ? AND status = 'ACTIVE' ORDER BY request_date DESC",
                new Object[]{borrowerId},
                (rs, idx) -> new HoldRow(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("borrower_user_id"),
                        rs.getTimestamp("request_date").toLocalDateTime(),
                        rs.getString("status")
                )
        );
    }

    public int countActiveHoldsForBorrower(int borrowerId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM hold_request WHERE borrower_user_id = ? AND status = 'ACTIVE'",
                Integer.class,
                borrowerId
        );
        return count == null ? 0 : count.intValue();
    }

    public int countAllActiveHolds() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM hold_request WHERE status = 'ACTIVE'",
                Integer.class
        );
        return count == null ? 0 : count.intValue();
    }

    public void cancelHold(int holdId, int borrowerId) {
        jdbcTemplate.update(
                "UPDATE hold_request SET status = 'CANCELLED' WHERE id = ? AND borrower_user_id = ?",
                holdId,
                borrowerId
        );
    }

    public static class HoldRow {
        private final int id;
        private final int bookId;
        private final int borrowerId;
        private final LocalDateTime requestDate;
        private final String status;

        public HoldRow(int id, int bookId, int borrowerId, LocalDateTime requestDate, String status) {
            this.id = id;
            this.bookId = bookId;
            this.borrowerId = borrowerId;
            this.requestDate = requestDate;
            this.status = status;
        }

        public int getId() { return id; }
        public int getBookId() { return bookId; }
        public int getBorrowerId() { return borrowerId; }
        public LocalDateTime getRequestDate() { return requestDate; }
        public String getStatus() { return status; }
    }
}
