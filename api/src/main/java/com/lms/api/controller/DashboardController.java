package com.lms.api.controller;

import com.lms.api.domain.Role;
import com.lms.api.dto.DashboardStatsDto;
import com.lms.api.infrastructure.session.SessionUser;
import com.lms.api.service.AuthService;
import com.lms.api.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    private final AuthService authService;

    public DashboardController(DashboardService dashboardService, AuthService authService) {
        this.dashboardService = dashboardService;
        this.authService = authService;
    }

    @GetMapping("/stats")
    public DashboardStatsDto stats(@RequestHeader(value = "Authorization", required = false) String authorization) {
        SessionUser user = authService.getSessionUser(authorization);
        authService.requireAnyRole(user, Role.ADMIN, Role.LIBRARIAN, Role.CLERK);
        return dashboardService.getStats();
    }
}
