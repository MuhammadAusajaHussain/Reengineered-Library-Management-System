package com.lms.api.infrastructure.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class BookRepository {
    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<BookRow> getAll() {
        return jdbcTemplate.query("SELECT id, isbn, title, author, subject, total_copies, available_copies FROM book ORDER BY id",
                (rs, idx) -> new BookRow(
                        rs.getInt("id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("subject"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                ));
    }

    public List<BookRow> search(String searchBy, String query) {
        String column = "title";
        if ("author".equals(searchBy)) {
            column = "author";
        } else if ("subject".equals(searchBy)) {
            column = "subject";
        }
        String sql = "SELECT id, isbn, title, author, subject, total_copies, available_copies FROM book " +
                "WHERE LOWER(" + column + ") LIKE ? ORDER BY id";
        return jdbcTemplate.query(sql, new Object[]{"%" + query.toLowerCase() + "%"},
                (rs, idx) -> new BookRow(
                        rs.getInt("id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("subject"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                ));
    }

    public BookRow findById(int id) {
        List<BookRow> rows = jdbcTemplate.query(
                "SELECT id, isbn, title, author, subject, total_copies, available_copies FROM book WHERE id = ?",
                new Object[]{id},
                (rs, idx) -> new BookRow(
                        rs.getInt("id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("subject"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                ));
        return rows.isEmpty() ? null : rows.get(0);
    }

    public int create(String isbn, String title, String author, String subject, int copies) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO book (isbn, title, author, subject, total_copies, available_copies) VALUES (?, ?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, isbn);
            statement.setString(2, title);
            statement.setString(3, author);
            statement.setString(4, subject);
            statement.setInt(5, copies);
            statement.setInt(6, copies);
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? 0 : key.intValue();
    }

    public void changeAvailability(int bookId, int delta) {
        jdbcTemplate.update(
                "UPDATE book SET available_copies = available_copies + ? WHERE id = ?",
                delta, bookId
        );
    }

    public static class BookRow {
        private final int id;
        private final String isbn;
        private final String title;
        private final String author;
        private final String subject;
        private final int totalCopies;
        private final int availableCopies;

        public BookRow(int id, String isbn, String title, String author, String subject, int totalCopies, int availableCopies) {
            this.id = id;
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.subject = subject;
            this.totalCopies = totalCopies;
            this.availableCopies = availableCopies;
        }

        public int getId() { return id; }
        public String getIsbn() { return isbn; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getSubject() { return subject; }
        public int getTotalCopies() { return totalCopies; }
        public int getAvailableCopies() { return availableCopies; }
    }
}
