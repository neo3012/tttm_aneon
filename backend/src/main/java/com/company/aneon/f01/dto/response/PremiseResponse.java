package com.company.aneon.f01.dto.response;

import com.company.aneon.f01.constant.PremiseStatus;
import com.company.aneon.f01.constant.PremiseType;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PremiseResponse(
        UUID id,
        String code,
        String name,
        UUID buildingId,
        String buildingName,
        UUID floorId,
        String floorName,
        UUID zoneId,
        String zoneName,
        BigDecimal totalArea,
        BigDecimal allocatedArea,
        BigDecimal availableArea,
        PremiseType type,
        PremiseStatus status,
        UUID locationId,
        BigDecimal x,
        BigDecimal y,
        String locationDescription,
        String description,
        Long version,
        Instant createdAt,
        Instant updatedAt) {
}

