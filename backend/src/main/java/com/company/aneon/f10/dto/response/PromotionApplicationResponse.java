package com.company.aneon.f10.dto.response;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record PromotionApplicationResponse(UUID id, UUID promotionId, UUID invoiceId, UUID customerId,
        BigDecimal discountAmount, Long pointsGranted, Instant appliedAt) {}
