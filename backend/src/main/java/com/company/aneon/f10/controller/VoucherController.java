package com.company.aneon.f10.controller;
import com.company.aneon.common.api.ApiResponse;
import com.company.aneon.f10.dto.request.CreateVoucherRequest;
import com.company.aneon.f10.dto.request.IssueVoucherRequest;
import com.company.aneon.f10.dto.response.VoucherIssueResponse;
import com.company.aneon.f10.dto.response.VoucherResponse;
import com.company.aneon.f10.service.PromotionService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/f10/vouchers")
public class VoucherController {
    private final PromotionService service;
    @PostMapping @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<VoucherResponse>> create(@Valid @RequestBody CreateVoucherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, "Voucher created", service.createVoucher(request)));
    }
    @PostMapping("/{id}/issues") @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<VoucherIssueResponse>> issue(@PathVariable UUID id,
            @RequestHeader("Idempotency-Key") String key, @Valid @RequestBody IssueVoucherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, "Voucher issued", service.issueVoucher(id, key, request)));
    }
}
