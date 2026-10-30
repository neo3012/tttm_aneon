package com.company.aneon.f01.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdatePremiseLocationRequest(
        @NotNull UUID locationId,
        @NotNull Long version,
        @Size(max = 500) String reason) {
}

