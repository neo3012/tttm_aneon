package com.company.aneon.f01.dto.request;

import com.company.aneon.f01.constant.PremiseType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePremiseRequest(
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 200) String name,
        @NotNull UUID zoneId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal totalArea,
        PremiseType type,
        UUID locationId,
        @Size(max = 1000) String description,
        @NotNull Long version) {
}

