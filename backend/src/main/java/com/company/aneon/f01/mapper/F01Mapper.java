package com.company.aneon.f01.mapper;

import com.company.aneon.f01.dto.response.CategoryResponse;
import com.company.aneon.f01.dto.response.HistoryResponse;
import com.company.aneon.f01.dto.response.PremiseResponse;
import com.company.aneon.f01.dto.response.StoreResponse;
import com.company.aneon.f01.dto.response.ZoneResponse;
import com.company.aneon.f01.entity.CuaHangSap;
import com.company.aneon.f01.entity.KhuVuc;
import com.company.aneon.f01.entity.LichSuMatBangVaCuaHang;
import com.company.aneon.f01.entity.MatBang;
import com.company.aneon.f01.entity.NganhHang;
import com.company.aneon.f01.entity.ViTri;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class F01Mapper {

    public PremiseResponse toPremiseResponse(MatBang entity, BigDecimal allocatedArea) {
        ViTri location = entity.getLocation();
        BigDecimal allocated = allocatedArea == null ? BigDecimal.ZERO : allocatedArea;
        return new PremiseResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getZone().getFloor().getBuilding().getId(),
                entity.getZone().getFloor().getBuilding().getName(),
                entity.getZone().getFloor().getId(),
                entity.getZone().getFloor().getName(),
                entity.getZone().getId(),
                entity.getZone().getName(),
                entity.getTotalArea(),
                allocated,
                entity.getTotalArea().subtract(allocated),
                entity.getType(),
                entity.getStatus(),
                location == null ? null : location.getId(),
                location == null ? null : location.getX(),
                location == null ? null : location.getY(),
                location == null ? null : location.getTextDescription(),
                entity.getDescription(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public StoreResponse toStoreResponse(CuaHangSap entity) {
        return new StoreResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getPremise().getId(),
                entity.getPremise().getCode(),
                entity.getArea(),
                entity.getCategory() == null ? null : entity.getCategory().getId(),
                entity.getCategory() == null ? null : entity.getCategory().getName(),
                entity.getLocation() == null ? null : entity.getLocation().getId(),
                entity.getStatus(),
                entity.getOperatingHours(),
                entity.getDescription(),
                entity.isPublicVisible(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public HistoryResponse toHistoryResponse(LichSuMatBangVaCuaHang entity) {
        return new HistoryResponse(
                entity.getId(),
                entity.getTargetType(),
                entity.getTargetId(),
                entity.getAction(),
                entity.getOldValueJson(),
                entity.getNewValueJson(),
                entity.getChangedBy(),
                entity.getReason(),
                entity.getCreatedAt());
    }

    public ZoneResponse toZoneResponse(KhuVuc entity) {
        return new ZoneResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getFloor().getId(),
                entity.getFloor().getName(),
                entity.getFloor().getLevel(),
                entity.getFloor().getBuilding().getId(),
                entity.getFloor().getBuilding().getName());
    }

    public CategoryResponse toCategoryResponse(NganhHang entity) {
        return new CategoryResponse(entity.getId(), entity.getCode(), entity.getName(), entity.getDescription());
    }
}

