package com.company.aneon.f01.controller;

import com.company.aneon.common.api.ApiResponse;
import com.company.aneon.common.api.PageResponse;
import com.company.aneon.f01.constant.StoreStatus;
import com.company.aneon.f01.dto.request.ChangeStoreStatusRequest;
import com.company.aneon.f01.dto.request.CreateStoreRequest;
import com.company.aneon.f01.dto.request.UpdateStoreRequest;
import com.company.aneon.f01.dto.response.CodeAvailabilityResponse;
import com.company.aneon.f01.dto.response.StoreResponse;
import com.company.aneon.f01.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/f01/stores")
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    @Operation(summary = "Tra cứu danh sách cửa hàng/sạp")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','OPERATIONS','ADMIN')")
    public ApiResponse<PageResponse<StoreResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID premiseId,
            @RequestParam(required = false) StoreStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(
                200, "Lấy danh sách cửa hàng thành công",
                storeService.list(keyword, premiseId, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết cửa hàng/sạp")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','OPERATIONS','ADMIN')")
    public ApiResponse<StoreResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(200, "Lấy thông tin cửa hàng thành công", storeService.get(id));
    }

    @GetMapping("/code-availability")
    @Operation(summary = "Kiểm tra mã cửa hàng/sạp")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','ADMIN')")
    public ApiResponse<CodeAvailabilityResponse> checkCode(
            @RequestParam @NotBlank String code,
            @RequestParam(required = false) UUID excludeId) {
        return ApiResponse.success(
                200, "Kiểm tra mã cửa hàng thành công",
                storeService.checkCodeAvailable(code, excludeId));
    }

    @PostMapping
    @Operation(summary = "Tạo cửa hàng/sạp")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<StoreResponse>> create(
            @Valid @RequestBody CreateStoreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Thêm cửa hàng thành công", storeService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật cửa hàng/sạp")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','ADMIN')")
    public ApiResponse<StoreResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStoreRequest request) {
        return ApiResponse.success(200, "Cập nhật cửa hàng thành công", storeService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái cửa hàng/sạp")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','ADMIN')")
    public ApiResponse<StoreResponse> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeStoreStatusRequest request) {
        return ApiResponse.success(
                200, "Cập nhật trạng thái cửa hàng thành công",
                storeService.changeStatus(id, request));
    }
}

