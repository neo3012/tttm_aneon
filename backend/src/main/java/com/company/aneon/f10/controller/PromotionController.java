package com.company.aneon.f10.controller;
import com.company.aneon.common.api.ApiResponse;
import com.company.aneon.common.api.PageResponse;
import com.company.aneon.f10.dto.request.ApplyPromotionRequest;
import com.company.aneon.f10.dto.request.CreatePromotionRequest;
import com.company.aneon.f10.dto.response.PromotionApplicationResponse;
import com.company.aneon.f10.dto.response.PromotionResponse;
import com.company.aneon.f10.service.PromotionService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/f10/promotions")
public class PromotionController {
    private final PromotionService service;
    @GetMapping @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ApiResponse<PageResponse<PromotionResponse>> list(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(200, "Promotions loaded", service.list(pageable));
    }
    @PostMapping @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<PromotionResponse>> create(@Valid @RequestBody CreatePromotionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, "Promotion created", service.createPromotion(request)));
    }
    @PostMapping("/{id}/applications") @PreAuthorize("hasAnyRole('EVENT_STAFF','MANAGER','ADMIN')")
    public ApiResponse<PromotionApplicationResponse> apply(@PathVariable UUID id,
            @RequestHeader("Idempotency-Key") String key, @Valid @RequestBody ApplyPromotionRequest request) {
        return ApiResponse.success(200, "Promotion applied", service.apply(id, key, request));
    }
}
