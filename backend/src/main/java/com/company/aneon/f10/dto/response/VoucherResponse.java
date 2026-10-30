package com.company.aneon.f10.dto.response;
import com.company.aneon.f10.constant.VoucherDiscountType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record VoucherResponse(UUID id, String code, String name, VoucherDiscountType discountType, BigDecimal value,
        BigDecimal maximumDiscount, BigDecimal minimumInvoiceAmount, int issuedLimit, int issuedCount,
        int usedCount, int perCustomerLimit, Instant validFrom, Instant expiresAt, boolean active, Long version) {}
