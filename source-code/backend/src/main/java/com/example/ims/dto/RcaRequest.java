package com.example.ims.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RcaRequest(
        @NotBlank String rootCause,
        @NotBlank String contributingFactors,
        @NotBlank String correctiveActions,
        @NotBlank String preventiveActions,
        @NotBlank @Size(max = 120) String owner,
        @NotNull @FutureOrPresent LocalDate dueDate,
        boolean approved
) {
}
