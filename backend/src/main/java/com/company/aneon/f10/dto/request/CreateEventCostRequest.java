package com.company.aneon.f10.dto.request;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record CreateEventCostRequest(@Size(max = 200) String supplierName, @Size(max = 100) String invoiceNumber,
        @NotNull @DecimalMin("0.01") BigDecimal amount, @NotNull Instant incurredAt,
        UUID attachmentId, @Size(max = 1000) String description) {}
