package com.lms.api.controller;

import com.lms.api.dto.AuthResponse;
import com.lms.api.dto.LoginRequest;
import com.lms.api.infrastructure.session.SessionUser;
import com.lms.api.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public AuthResponse me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        SessionUser user = authService.getSessionUser(authorization);
        return new AuthResponse("", user.getUserId(), user.getUsername(), user.getFullName(), user.getRole().name());
    }
}
