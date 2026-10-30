package com.company.aneon.f10.dto.request;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
public record IssueVoucherRequest(@NotNull UUID customerId, UUID promotionId, @Size(max = 200) String reference) {}
