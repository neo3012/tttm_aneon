package com.company.aneon.f10.dto.response;

import com.company.aneon.f10.constant.EventStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EventResponse(
        UUID id, String code, String name, String type, UUID locationId,
        Instant startAt, Instant endAt, BigDecimal estimatedBudget, BigDecimal approvedBudget,
        Integer targetAttendees, EventStatus status, UUID proposedBy, UUID approvedBy,
        Instant approvedAt, String rejectionReason, boolean publicVisible, Long version) {}
