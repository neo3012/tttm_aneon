package com.company.aneon.f01.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateStoreRequest(
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 200) String name,
        @NotNull UUID premiseId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal area,
        UUID categoryId,
        UUID locationId,
        @Size(max = 200) String operatingHours,
        @Size(max = 1000) String description,
        boolean publicVisible,
        @NotNull Long version) {
}

