package com.company.aneon.f01.dto.response;

import com.company.aneon.f01.constant.StoreStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StoreResponse(
        UUID id,
        String code,
        String name,
        UUID premiseId,
        String premiseCode,
        BigDecimal area,
        UUID categoryId,
        String categoryName,
        UUID locationId,
        StoreStatus status,
        String operatingHours,
        String description,
        boolean publicVisible,
        Long version,
        Instant createdAt,
        Instant updatedAt) {
}

