package com.example.ims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DelegateRequest(
        @NotBlank @Size(max = 120) String incidentCommander,
        @NotBlank @Size(max = 120) String communicationLead,
        @NotBlank @Size(max = 120) String technicalLead,
        @NotBlank @Size(max = 120) String escalationManager,
        @NotBlank @Size(max = 120) String seniorManager,
        @NotBlank String delegationNote
) {
}
