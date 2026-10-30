package com.company.aneon.f01.dto.response;

import java.time.Instant;
import java.util.UUID;

public record HistoryResponse(
        UUID id,
        String targetType,
        UUID targetId,
        String action,
        String oldValueJson,
        String newValueJson,
        UUID changedBy,
        String reason,
        Instant createdAt) {
}

