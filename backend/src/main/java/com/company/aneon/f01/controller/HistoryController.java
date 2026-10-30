package com.company.aneon.f01.controller;

import com.company.aneon.common.api.ApiResponse;
import com.company.aneon.common.api.PageResponse;
import com.company.aneon.f01.dto.response.HistoryResponse;
import com.company.aneon.f01.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/f01/history")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    @Operation(summary = "Tra cứu lịch sử mặt bằng/cửa hàng")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','OPERATIONS','ADMIN')")
    public ApiResponse<PageResponse<HistoryResponse>> list(
            @RequestParam String targetType,
            @RequestParam UUID targetId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(
                200, "Lấy lịch sử thay đổi thành công",
                historyService.list(targetType, targetId, from, to, pageable));
    }
}

