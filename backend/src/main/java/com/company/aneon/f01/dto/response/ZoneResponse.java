package com.company.aneon.f01.dto.response;

import java.util.UUID;

public record ZoneResponse(
        UUID id,
        String code,
        String name,
        UUID floorId,
        String floorName,
        int floorLevel,
        UUID buildingId,
        String buildingName) {
}

