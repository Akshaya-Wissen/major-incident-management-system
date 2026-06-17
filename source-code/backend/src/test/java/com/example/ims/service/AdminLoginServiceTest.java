package com.example.ims.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.ims.dto.AdminLoginRequest;
import com.example.ims.dto.AdminRegisterRequest;
import com.example.ims.entity.AppUser;
import com.example.ims.exception.InvalidIncidentTransitionException;
import com.example.ims.repository.AppUserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AdminLoginServiceTest {
    private AppUserRepository appUserRepository;
    private PasswordHasher passwordHasher;
    private AdminLoginService service;

    @BeforeEach
    void setUp() {
        appUserRepository = Mockito.mock(AppUserRepository.class);
        passwordHasher = Mockito.mock(PasswordHasher.class);
        service = new AdminLoginService(appUserRepository, passwordHasher, "admin", "admin123", "Demo Administrator");
    }

    @Test
    void acceptsConfiguredAdminCredentials() {
        var response = service.login(new AdminLoginRequest("admin", "admin123"));

        assertThat(response.username()).isEqualTo("admin");
        assertThat(response.displayName()).isEqualTo("Demo Administrator");
        assertThat(response.role()).isEqualTo("ADMIN");
    }

    @Test
    void acceptsRegisteredUserCredentials() {
        AppUser user = user("maya.singh", "Maya Singh", "TEAM_LEAD", "stored-hash");
        when(appUserRepository.findByUsernameIgnoreCase("maya.singh")).thenReturn(Optional.of(user));
        when(passwordHasher.verify("secure123", "stored-hash")).thenReturn(true);

        var response = service.login(new AdminLoginRequest("maya.singh", "secure123"));

        assertThat(response.username()).isEqualTo("maya.singh");
        assertThat(response.displayName()).isEqualTo("Maya Singh");
        assertThat(response.role()).isEqualTo("TEAM_LEAD");
    }

    @Test
    void rejectsInvalidAdminCredentials() {
        assertThatThrownBy(() -> service.login(new AdminLoginRequest("admin", "wrong")))
                .isInstanceOf(InvalidIncidentTransitionException.class);
    }

    @Test
    void registersUserWithHashedPassword() {
        when(appUserRepository.existsByUsernameIgnoreCase("anika.rao")).thenReturn(false);
        when(passwordHasher.hash("secure123")).thenReturn("new-hash");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.register(new AdminRegisterRequest(
                "Anika.Rao",
                "Anika Rao",
                "secure123",
                "ESCALATION_MANAGER"
        ));

        assertThat(response.username()).isEqualTo("anika.rao");
        assertThat(response.displayName()).isEqualTo("Anika Rao");
        assertThat(response.role()).isEqualTo("ESCALATION_MANAGER");
    }

    @Test
    void rejectsDuplicateRegistration() {
        when(appUserRepository.existsByUsernameIgnoreCase("admin")).thenReturn(false);

        assertThatThrownBy(() -> service.register(new AdminRegisterRequest(
                "admin",
                "Someone",
                "secure123",
                "TEAM_LEAD"
        ))).isInstanceOf(InvalidIncidentTransitionException.class);
    }

    private AppUser user(String username, String displayName, String role, String passwordHash) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setRole(role);
        user.setPasswordHash(passwordHash);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
