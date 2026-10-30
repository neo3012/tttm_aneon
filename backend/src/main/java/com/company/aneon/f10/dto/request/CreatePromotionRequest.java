package com.company.aneon.f10.dto.request;
import com.company.aneon.f10.constant.PromotionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
public record CreatePromotionRequest(
        @NotBlank @Size(max = 50) String code, @NotBlank @Size(max = 200) String name,
        @NotNull PromotionType type, @NotNull Instant startAt, @NotNull Instant endAt,
        @DecimalMin("0") BigDecimal minimumInvoiceAmount, Set<String> targetTiers,
        String terms, boolean publicVisible,
        @DecimalMin("0.0001") BigDecimal discountRate, @DecimalMin("0") BigDecimal maximumDiscount,
        UUID voucherId, @Positive Integer totalToIssue, @Positive Long rewardPoints,
        @DecimalMin("0.01") BigDecimal conversionAmount, @Positive Integer pointsPerConversion,
        Set<UUID> storeIds) {}
