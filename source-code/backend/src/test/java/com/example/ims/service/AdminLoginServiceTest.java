package com.example.ims.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.ims.dto.AdminLoginRequest;
import com.example.ims.exception.InvalidIncidentTransitionException;
import org.junit.jupiter.api.Test;

class AdminLoginServiceTest {
    private final AdminLoginService service = new AdminLoginService("admin", "admin123", "Demo Administrator");

    @Test
    void acceptsConfiguredAdminCredentials() {
        var response = service.login(new AdminLoginRequest("admin", "admin123"));

        assertThat(response.username()).isEqualTo("admin");
        assertThat(response.displayName()).isEqualTo("Demo Administrator");
        assertThat(response.role()).isEqualTo("ADMIN");
    }

    @Test
    void acceptsDemoRoleCredentials() {
        var response = service.login(new AdminLoginRequest("nikhil.batra", "demo123"));

        assertThat(response.displayName()).isEqualTo("Nikhil Batra");
        assertThat(response.role()).isEqualTo("SENIOR_MANAGER");
    }

    @Test
    void rejectsInvalidAdminCredentials() {
        assertThatThrownBy(() -> service.login(new AdminLoginRequest("admin", "wrong")))
                .isInstanceOf(InvalidIncidentTransitionException.class);
    }
}
