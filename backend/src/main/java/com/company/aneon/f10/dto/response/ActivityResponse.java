package com.company.aneon.f10.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ActivityResponse(
        UUID id, UUID eventId, String name, Instant startAt, Instant endAt,
        UUID locationId, UUID personInCharge, String description, Long version) {}
