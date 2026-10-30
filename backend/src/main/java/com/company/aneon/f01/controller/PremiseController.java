package com.company.aneon.f01.controller;

import com.company.aneon.common.api.ApiResponse;
import com.company.aneon.common.api.PageResponse;
import com.company.aneon.f01.constant.PremiseStatus;
import com.company.aneon.f01.dto.request.ChangePremiseStatusRequest;
import com.company.aneon.f01.dto.request.CreatePremiseRequest;
import com.company.aneon.f01.dto.request.UpdatePremiseLocationRequest;
import com.company.aneon.f01.dto.request.UpdatePremiseRequest;
import com.company.aneon.f01.dto.response.CodeAvailabilityResponse;
import com.company.aneon.f01.dto.response.PremiseResponse;
import com.company.aneon.f01.service.PremiseService;
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
@RequestMapping("/api/v1/f01/premises")
public class PremiseController {

    private final PremiseService premiseService;

    @GetMapping
    @Operation(summary = "Tra cứu danh sách mặt bằng")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','OPERATIONS','ADMIN')")
    public ApiResponse<PageResponse<PremiseResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID floorId,
            @RequestParam(required = false) UUID zoneId,
            @RequestParam(required = false) PremiseStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(
                200, "Lấy danh sách mặt bằng thành công",
                premiseService.list(keyword, floorId, zoneId, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết mặt bằng")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','OPERATIONS','ADMIN')")
    public ApiResponse<PremiseResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(200, "Lấy thông tin mặt bằng thành công", premiseService.get(id));
    }

    @GetMapping("/code-availability")
    @Operation(summary = "Kiểm tra mã mặt bằng")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','ADMIN')")
    public ApiResponse<CodeAvailabilityResponse> checkCode(
            @RequestParam @NotBlank String code,
            @RequestParam(required = false) UUID excludeId) {
        return ApiResponse.success(
                200, "Kiểm tra mã mặt bằng thành công",
                premiseService.checkCodeAvailable(code, excludeId));
    }

    @PostMapping
    @Operation(summary = "Tạo mặt bằng")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<PremiseResponse>> create(
            @Valid @RequestBody CreatePremiseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Thêm mặt bằng thành công", premiseService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật mặt bằng")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','ADMIN')")
    public ApiResponse<PremiseResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePremiseRequest request) {
        return ApiResponse.success(200, "Cập nhật mặt bằng thành công", premiseService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái mặt bằng")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','OPERATIONS','ADMIN')")
    public ApiResponse<PremiseResponse> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePremiseStatusRequest request) {
        return ApiResponse.success(
                200, "Cập nhật trạng thái mặt bằng thành công",
                premiseService.changeStatus(id, request));
    }

    @PatchMapping("/{id}/location")
    @Operation(summary = "Cập nhật vị trí mặt bằng trên bản đồ")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','ADMIN')")
    public ApiResponse<PremiseResponse> updateLocation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePremiseLocationRequest request) {
        return ApiResponse.success(
                200, "Cập nhật vị trí mặt bằng thành công",
                premiseService.updateLocation(id, request));
    }

    @PostMapping("/{id}/sync-contract-status")
    @Operation(summary = "Đồng bộ trạng thái mặt bằng theo hợp đồng")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','OPERATIONS','ADMIN')")
    public ApiResponse<PremiseResponse> syncContractStatus(@PathVariable UUID id) {
        return ApiResponse.success(
                200, "Đồng bộ trạng thái mặt bằng thành công",
                premiseService.syncStatusFromContract(id));
    }
}

