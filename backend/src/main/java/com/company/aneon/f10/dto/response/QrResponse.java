package com.company.aneon.f10.dto.response;

import java.time.Instant;
import java.util.UUID;

public record QrResponse(UUID qrId, UUID eventId, String token, String checkInUrl, Instant expiresAt) {}
