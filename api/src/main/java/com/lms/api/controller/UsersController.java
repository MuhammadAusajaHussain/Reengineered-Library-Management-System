package com.lms.api.controller;

import com.lms.api.domain.Role;
import com.lms.api.dto.CreateUserRequest;
import com.lms.api.dto.UpdateUserRequest;
import com.lms.api.dto.UserDto;
import com.lms.api.infrastructure.session.SessionUser;
import com.lms.api.service.AuthService;
import com.lms.api.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UserService userService;
    private final AuthService authService;

    public UsersController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public List<UserDto> listUsers(@RequestHeader(value = "Authorization", required = false) String authorization) {
        SessionUser user = authService.getSessionUser(authorization);
        authService.requireAnyRole(user, Role.ADMIN);
        return userService.listUsers();
    }

    @PostMapping
    public UserDto createUser(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody CreateUserRequest request
    ) {
        SessionUser user = authService.getSessionUser(authorization);
        authService.requireAnyRole(user, Role.ADMIN);
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable int id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        SessionUser user = authService.getSessionUser(authorization);
        authService.requireAnyRole(user, Role.ADMIN);
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable int id
    ) {
        SessionUser user = authService.getSessionUser(authorization);
        authService.requireAnyRole(user, Role.ADMIN);
        userService.deleteUser(id);
    }
}

