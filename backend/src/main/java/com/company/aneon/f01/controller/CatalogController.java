package com.company.aneon.f01.controller;

import com.company.aneon.common.api.ApiResponse;
import com.company.aneon.f01.dto.response.CategoryResponse;
import com.company.aneon.f01.dto.response.ZoneResponse;
import com.company.aneon.f01.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/f01/catalogs")
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/zones")
    @Operation(summary = "Lấy danh mục khu vực đang hoạt động")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','OPERATIONS','ADMIN')")
    public ApiResponse<List<ZoneResponse>> zones() {
        return ApiResponse.success(200, "Lấy danh mục khu vực thành công", catalogService.listZones());
    }

    @GetMapping("/categories")
    @Operation(summary = "Lấy danh mục ngành hàng")
    @PreAuthorize("hasAnyRole('PREMISE_MANAGER','OPERATIONS','ADMIN')")
    public ApiResponse<List<CategoryResponse>> categories() {
        return ApiResponse.success(200, "Lấy danh mục ngành hàng thành công", catalogService.listCategories());
    }
}

