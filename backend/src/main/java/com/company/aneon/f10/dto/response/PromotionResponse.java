package com.company.aneon.f10.dto.response;
import com.company.aneon.f10.constant.PromotionStatus;
import com.company.aneon.f10.constant.PromotionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record PromotionResponse(UUID id, String code, String name, PromotionType type, Instant startAt, Instant endAt,
        BigDecimal minimumInvoiceAmount, PromotionStatus status, boolean publicVisible, Long version) {}
