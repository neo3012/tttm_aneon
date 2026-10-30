package com.company.aneon.f10.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CheckInResponse(UUID attendanceId, UUID eventId, UUID customerId, Instant checkInAt) {}
