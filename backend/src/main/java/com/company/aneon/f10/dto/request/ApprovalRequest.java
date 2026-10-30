package com.company.aneon.f10.dto.request;

import com.company.aneon.f10.constant.EventStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ApprovalRequest(
        @NotNull EventStatus decision,
        @DecimalMin("0.00") BigDecimal approvedBudget,
        @Size(max = 500) String rejectionReason,
        @NotNull Long version) {}
