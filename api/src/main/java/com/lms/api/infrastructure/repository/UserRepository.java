package com.lms.api.infrastructure.repository;

import com.lms.api.domain.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserRow findByUsername(String username) {
        List<UserRow> rows = jdbcTemplate.query(
                "SELECT id, username, password_hash, full_name, role, active FROM app_user WHERE username = ?",
                new Object[]{username},
                (rs, idx) -> new UserRow(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        Role.valueOf(rs.getString("role")),
                        rs.getBoolean("active")
                )
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    public UserRow findById(int userId) {
        List<UserRow> rows = jdbcTemplate.query(
                "SELECT id, username, password_hash, full_name, role, active FROM app_user WHERE id = ?",
                new Object[]{userId},
                (rs, idx) -> new UserRow(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        Role.valueOf(rs.getString("role")),
                        rs.getBoolean("active")
                )
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    public int createBorrower(String username, String passwordHash, String fullName, String address, String phoneNo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO app_user (username, password_hash, full_name, role, active) VALUES (?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, fullName);
            statement.setString(4, Role.BORROWER.name());
            statement.setBoolean(5, true);
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        int userId = key == null ? 0 : key.intValue();
        jdbcTemplate.update(
                "INSERT INTO borrower_profile (user_id, address, phone_no) VALUES (?, ?, ?)",
                userId, address, phoneNo
        );
        return userId;
    }

    public BorrowerProfileRow findBorrowerById(int borrowerId) {
        List<BorrowerProfileRow> rows = jdbcTemplate.query(
                "SELECT u.id, u.full_name, bp.address, bp.phone_no FROM app_user u " +
                        "INNER JOIN borrower_profile bp ON u.id = bp.user_id " +
                        "WHERE u.id = ? AND u.role = 'BORROWER'",
                new Object[]{borrowerId},
                (rs, idx) -> new BorrowerProfileRow(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("address"),
                        rs.getString("phone_no")
                )
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<BorrowerProfileRow> getBorrowers() {
        return jdbcTemplate.query(
                "SELECT u.id, u.full_name, bp.address, bp.phone_no FROM app_user u " +
                        "INNER JOIN borrower_profile bp ON u.id = bp.user_id " +
                        "WHERE u.role = 'BORROWER' ORDER BY u.id",
                (rs, idx) -> new BorrowerProfileRow(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("address"),
                        rs.getString("phone_no")
                )
        );
    }

    public List<UserSummaryRow> listUsers() {
        return jdbcTemplate.query(
                "SELECT id, username, full_name, role, active FROM app_user ORDER BY id",
                (rs, idx) -> new UserSummaryRow(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        Role.valueOf(rs.getString("role")),
                        rs.getBoolean("active")
                )
        );
    }

    public int createUser(String username, String passwordHash, String fullName, Role role, boolean active) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO app_user (username, password_hash, full_name, role, active) VALUES (?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, fullName);
            statement.setString(4, role.name());
            statement.setBoolean(5, active);
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? 0 : key.intValue();
    }

    public void updateUser(int userId, String fullName, Role role, Boolean active) {
        if (fullName != null) {
            jdbcTemplate.update("UPDATE app_user SET full_name = ? WHERE id = ?", fullName, userId);
        }
        if (role != null) {
            jdbcTemplate.update("UPDATE app_user SET role = ? WHERE id = ?", role.name(), userId);
        }
        if (active != null) {
            jdbcTemplate.update("UPDATE app_user SET active = ? WHERE id = ?", active.booleanValue(), userId);
        }
    }

    public void updatePassword(int userId, String passwordHash) {
        jdbcTemplate.update("UPDATE app_user SET password_hash = ? WHERE id = ?", passwordHash, userId);
    }

    public void deleteUser(int userId) {
        jdbcTemplate.update("DELETE FROM borrower_profile WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM hold_request WHERE borrower_user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM loan WHERE borrower_user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM loan WHERE issued_by_user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM loan WHERE returned_by_user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM app_user WHERE id = ?", userId);
    }

    public int countBorrowers() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM app_user WHERE role = 'BORROWER'",
                Integer.class
        );
        return count == null ? 0 : count.intValue();
    }

    public static class UserRow {
        private final int id;
        private final String username;
        private final String passwordHash;
        private final String fullName;
        private final Role role;
        private final boolean active;

        public UserRow(int id, String username, String passwordHash, String fullName, Role role, boolean active) {
            this.id = id;
            this.username = username;
            this.passwordHash = passwordHash;
            this.fullName = fullName;
            this.role = role;
            this.active = active;
        }

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getPasswordHash() { return passwordHash; }
        public String getFullName() { return fullName; }
        public Role getRole() { return role; }
        public boolean isActive() { return active; }
    }

    public static class BorrowerProfileRow {
        private final int id;
        private final String fullName;
        private final String address;
        private final String phoneNo;

        public BorrowerProfileRow(int id, String fullName, String address, String phoneNo) {
            this.id = id;
            this.fullName = fullName;
            this.address = address;
            this.phoneNo = phoneNo;
        }

        public int getId() { return id; }
        public String getFullName() { return fullName; }
        public String getAddress() { return address; }
        public String getPhoneNo() { return phoneNo; }
    }

    public static class UserSummaryRow {
        private final int id;
        private final String username;
        private final String fullName;
        private final Role role;
        private final boolean active;

        public UserSummaryRow(int id, String username, String fullName, Role role, boolean active) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.role = role;
            this.active = active;
        }

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public Role getRole() { return role; }
        public boolean isActive() { return active; }
    }
}
