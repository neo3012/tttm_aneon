package com.company.aneon.f10.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateEventRequest(
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 100) String type,
        UUID locationId,
        @NotNull Instant startAt,
        @NotNull @Future Instant endAt,
        @DecimalMin("0.00") BigDecimal estimatedBudget,
        @PositiveOrZero Integer targetAttendees,
        boolean publicVisible) {}
