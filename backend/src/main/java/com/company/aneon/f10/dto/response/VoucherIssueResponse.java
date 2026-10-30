package com.company.aneon.f10.dto.response;
import com.company.aneon.f10.constant.VoucherIssueStatus;
import java.time.Instant;
import java.util.UUID;
public record VoucherIssueResponse(UUID id, UUID voucherId, UUID customerId, VoucherIssueStatus status, Instant issuedAt) {}
