package com.company.aneon.f10.dto.request;
import com.company.aneon.f10.constant.VoucherDiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
public record CreateVoucherRequest(
        @NotBlank @Size(max = 50) String code, @NotBlank @Size(max = 200) String name,
        @NotNull VoucherDiscountType discountType, @NotNull @DecimalMin("0.01") BigDecimal value,
        @DecimalMin("0") BigDecimal maximumDiscount, @DecimalMin("0") BigDecimal minimumInvoiceAmount,
        @NotNull @Positive Integer issuedLimit, @NotNull @Positive Integer perCustomerLimit,
        @NotNull Instant validFrom, @NotNull Instant expiresAt, Set<UUID> storeIds) {}
