package com.company.aneon.f10.dto.response;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record EventCostResponse(UUID id, UUID eventId, String supplierName, String invoiceNumber,
        BigDecimal amount, Instant incurredAt, UUID attachmentId, String description) {}
