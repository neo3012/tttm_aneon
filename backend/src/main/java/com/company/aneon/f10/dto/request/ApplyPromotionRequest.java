package com.company.aneon.f10.dto.request;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record ApplyPromotionRequest(@NotNull UUID invoiceId, @NotNull UUID customerId, UUID customerVoucherId) {}
