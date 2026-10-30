package com.company.aneon.f10.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CheckInRequest(
        @NotBlank String token,
        @NotNull UUID customerId,
        @Size(max = 200) String scanningDevice) {}
