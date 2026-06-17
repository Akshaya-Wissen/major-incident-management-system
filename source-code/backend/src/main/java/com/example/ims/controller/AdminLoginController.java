package com.example.ims.controller;

import com.example.ims.dto.AdminLoginRequest;
import com.example.ims.dto.AdminLoginResponse;
import com.example.ims.dto.AdminRegisterRequest;
import com.example.ims.service.AdminLoginService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {
    private final AdminLoginService adminLoginService;

    public AdminLoginController(AdminLoginService adminLoginService) {
        this.adminLoginService = adminLoginService;
    }

    @PostMapping("/login")
    public AdminLoginResponse login(@Valid @RequestBody AdminLoginRequest request) {
        return adminLoginService.login(request);
    }

    @PostMapping("/register")
    public AdminLoginResponse register(@Valid @RequestBody AdminRegisterRequest request) {
        return adminLoginService.register(request);
    }
}
