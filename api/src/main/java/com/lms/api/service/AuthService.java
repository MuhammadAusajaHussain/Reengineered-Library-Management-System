package com.lms.api.service;

import com.lms.api.domain.Role;
import com.lms.api.dto.AuthResponse;
import com.lms.api.dto.LoginRequest;
import com.lms.api.exception.ForbiddenException;
import com.lms.api.exception.UnauthorizedException;
import com.lms.api.infrastructure.repository.UserRepository;
import com.lms.api.infrastructure.session.SessionStore;
import com.lms.api.infrastructure.session.SessionUser;
import com.lms.api.util.PasswordHasher;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final SessionStore sessionStore;

    public AuthService(UserRepository userRepository, SessionStore sessionStore) {
        this.userRepository = userRepository;
        this.sessionStore = sessionStore;
    }

    public AuthResponse login(LoginRequest request) {
        UserRepository.UserRow user = userRepository.findByUsername(request.getUsername().trim());
        if (user == null) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String hash = PasswordHasher.sha256(request.getPassword());
        if (!hash.equals(user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is inactive");
        }

        SessionUser sessionUser = new SessionUser(user.getId(), user.getUsername(), user.getFullName(), user.getRole());
        String token = sessionStore.createSession(sessionUser);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getFullName(), user.getRole().name());
    }

    public SessionUser getSessionUser(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        SessionUser sessionUser = sessionStore.getUser(token);
        if (sessionUser == null) {
            throw new UnauthorizedException("Session has expired or token is invalid");
        }
        return sessionUser;
    }

    public void requireAnyRole(SessionUser user, Role... allowedRoles) {
        for (Role role : allowedRoles) {
            if (user.getRole() == role) {
                return;
            }
        }
        throw new ForbiddenException("You do not have permission to perform this action");
    }
}
