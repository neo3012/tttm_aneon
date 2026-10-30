package com.company.aneon.f10.dto.response;
import java.math.BigDecimal;
import java.util.UUID;
public record EventRoiResponse(UUID eventId, BigDecimal approvedBudget, BigDecimal actualCost,
        BigDecimal budgetVariance, BigDecimal varianceRate, boolean zeroCost) {}
