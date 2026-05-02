package com.lms.api.config;

import com.lms.api.domain.Role;
import com.lms.api.infrastructure.repository.UserRepository;
import com.lms.api.util.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BootstrapAdminInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminInitializer.class);

    private final BootstrapAdminProperties properties;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public BootstrapAdminInitializer(BootstrapAdminProperties properties, UserRepository userRepository, JdbcTemplate jdbcTemplate) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        String username = safe(properties.getUsername(), "admin");
        String password = safe(properties.getPassword(), "admin");
        String fullName = safe(properties.getFullName(), "System Admin");
        String passwordHash = PasswordHasher.sha256(password);

        UserRepository.UserRow existing = userRepository.findByUsername(username);
        if (existing == null) {
            jdbcTemplate.update(
                    "INSERT INTO app_user (username, password_hash, full_name, role, active) VALUES (?, ?, ?, ?, ?)",
                    username, passwordHash, fullName, Role.ADMIN.name(), true
            );
            log.info("Bootstrapped admin user '{}'.", username);
            return;
        }

        if (properties.isResetPassword() && !passwordHash.equals(existing.getPasswordHash())) {
            jdbcTemplate.update(
                    "UPDATE app_user SET password_hash = ? WHERE id = ?",
                    passwordHash,
                    existing.getId()
            );
            log.info("Reset password for admin user '{}'.", username);
        }
    }

    private static String safe(String value, String fallback) {
        if (value == null) return fallback;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }
}

