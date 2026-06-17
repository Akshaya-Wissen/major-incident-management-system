package com.example.ims.service;

import com.example.ims.dto.AdminLoginRequest;
import com.example.ims.dto.AdminRegisterRequest;
import com.example.ims.dto.AdminLoginResponse;
import com.example.ims.entity.AppUser;
import com.example.ims.exception.InvalidIncidentTransitionException;
import com.example.ims.repository.AppUserRepository;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminLoginService {
    private static final Set<String> ALLOWED_REGISTER_ROLES = Set.of(
            "TEAM_LEAD",
            "ESCALATION_MANAGER",
            "SENIOR_MANAGER",
            "TECHNICAL_LEAD",
            "COMMUNICATION_LEAD"
    );

    private final AppUserRepository appUserRepository;
    private final PasswordHasher passwordHasher;
    private final String adminUsername;
    private final String adminPassword;
    private final String adminDisplayName;

    public AdminLoginService(
            AppUserRepository appUserRepository,
            PasswordHasher passwordHasher,
            @Value("${app.admin.username:admin}") String adminUsername,
            @Value("${app.admin.password:admin123}") String adminPassword,
            @Value("${app.admin.display-name:Demo Administrator}") String adminDisplayName
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordHasher = passwordHasher;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminDisplayName = adminDisplayName;
    }

    public AdminLoginResponse login(AdminLoginRequest request) {
        String username = normalizeUsername(request.username());
        if (adminUsername.equals(username) && adminPassword.equals(request.password())) {
            return new AdminLoginResponse(adminUsername, adminDisplayName, "ADMIN");
        }

        AppUser user = appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new InvalidIncidentTransitionException("Invalid username or password."));
        if (!passwordHasher.verify(request.password(), user.getPasswordHash())) {
            throw new InvalidIncidentTransitionException("Invalid username or password.");
        }
        return toResponse(user);
    }

    public AdminLoginResponse register(AdminRegisterRequest request) {
        String username = normalizeUsername(request.username());
        String role = normalizeRole(request.role());
        if (adminUsername.equalsIgnoreCase(username) || appUserRepository.existsByUsernameIgnoreCase(username)) {
            throw new InvalidIncidentTransitionException("Username is already registered.");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setDisplayName(request.displayName().trim());
        user.setPasswordHash(passwordHasher.hash(request.password()));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        return toResponse(appUserRepository.save(user));
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase();
    }

    private String normalizeRole(String role) {
        String normalized = role.trim().toUpperCase();
        if (!ALLOWED_REGISTER_ROLES.contains(normalized)) {
            throw new InvalidIncidentTransitionException("Select a valid role for registration.");
        }
        return normalized;
    }

    private AdminLoginResponse toResponse(AppUser user) {
        return new AdminLoginResponse(user.getUsername(), user.getDisplayName(), user.getRole());
    }
}
