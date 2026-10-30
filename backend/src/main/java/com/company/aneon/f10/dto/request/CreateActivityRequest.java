package com.company.aneon.f10.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public record CreateActivityRequest(
        @NotBlank @Size(max = 200) String name,
        @NotNull Instant startAt,
        @NotNull Instant endAt,
        @NotNull UUID locationId,
        UUID personInCharge,
        @Size(max = 1000) String description) {}
