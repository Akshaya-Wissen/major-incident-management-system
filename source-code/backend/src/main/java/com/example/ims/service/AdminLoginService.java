package com.example.ims.service;

import com.example.ims.dto.AdminLoginRequest;
import com.example.ims.dto.AdminLoginResponse;
import com.example.ims.exception.InvalidIncidentTransitionException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminLoginService {
    private static final String DEMO_PASSWORD = "demo123";
    private static final List<DemoUser> DEMO_USERS = List.of(
            new DemoUser("aarav.mehta", "Aarav Mehta", "TEAM_LEAD"),
            new DemoUser("maya.singh", "Maya Singh", "TEAM_LEAD"),
            new DemoUser("priya.menon", "Priya Menon", "TEAM_LEAD"),
            new DemoUser("anika.rao", "Anika Rao", "ESCALATION_MANAGER"),
            new DemoUser("karan.malhotra", "Karan Malhotra", "ESCALATION_MANAGER"),
            new DemoUser("meera.joshi", "Meera Joshi", "ESCALATION_MANAGER"),
            new DemoUser("pooja.shah", "Pooja Shah", "ESCALATION_MANAGER"),
            new DemoUser("nikhil.batra", "Nikhil Batra", "SENIOR_MANAGER"),
            new DemoUser("rehan.kapur", "Rehan Kapur", "SENIOR_MANAGER"),
            new DemoUser("sameer.desai", "Sameer Desai", "SENIOR_MANAGER"),
            new DemoUser("sana.khan", "Sana Khan", "SENIOR_MANAGER"),
            new DemoUser("dev.patel", "Dev Patel", "TECHNICAL_LEAD"),
            new DemoUser("fatima.ali", "Fatima Ali", "TECHNICAL_LEAD"),
            new DemoUser("sara.dsouza", "Sara Dsouza", "TECHNICAL_LEAD"),
            new DemoUser("tara.bose", "Tara Bose", "TECHNICAL_LEAD"),
            new DemoUser("vikram.shah", "Vikram Shah", "TECHNICAL_LEAD"),
            new DemoUser("isha.nair", "Isha Nair", "COMMUNICATION_LEAD"),
            new DemoUser("neha.rao", "Neha Rao", "COMMUNICATION_LEAD"),
            new DemoUser("om.prakash", "Om Prakash", "COMMUNICATION_LEAD"),
            new DemoUser("rohan.iyer", "Rohan Iyer", "COMMUNICATION_LEAD")
    );

    private final String adminUsername;
    private final String adminPassword;
    private final String adminDisplayName;

    public AdminLoginService(
            @Value("${app.admin.username:admin}") String adminUsername,
            @Value("${app.admin.password:admin123}") String adminPassword,
            @Value("${app.admin.display-name:Demo Administrator}") String adminDisplayName
    ) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminDisplayName = adminDisplayName;
    }

    public AdminLoginResponse login(AdminLoginRequest request) {
        if (!adminUsername.equals(request.username()) || !adminPassword.equals(request.password())) {
            return DEMO_USERS.stream()
                    .filter(user -> user.username().equals(request.username()) && DEMO_PASSWORD.equals(request.password()))
                    .findFirst()
                    .map(user -> new AdminLoginResponse(user.username(), user.displayName(), user.role()))
                    .orElseThrow(() -> new InvalidIncidentTransitionException("Invalid username or password."));
        }
        return new AdminLoginResponse(adminUsername, adminDisplayName, "ADMIN");
    }

    private record DemoUser(String username, String displayName, String role) {
    }
}
