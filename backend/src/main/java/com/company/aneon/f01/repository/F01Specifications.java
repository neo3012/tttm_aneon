package com.company.aneon.f01.repository;

import com.company.aneon.f01.constant.PremiseStatus;
import com.company.aneon.f01.constant.StoreStatus;
import com.company.aneon.f01.entity.CuaHangSap;
import com.company.aneon.f01.entity.MatBang;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class F01Specifications {

    private F01Specifications() {
    }

    public static Specification<MatBang> premises(
            String keyword,
            UUID floorId,
            UUID zoneId,
            PremiseStatus status,
            Set<UUID> allowedZoneIds) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String term = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("code")), term),
                        builder.like(builder.lower(root.get("name")), term)));
            }
            if (floorId != null) {
                predicates.add(builder.equal(root.get("zone").get("floor").get("id"), floorId));
            }
            if (zoneId != null) {
                predicates.add(builder.equal(root.get("zone").get("id"), zoneId));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (allowedZoneIds != null && !allowedZoneIds.isEmpty()) {
                predicates.add(root.get("zone").get("id").in(allowedZoneIds));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<CuaHangSap> stores(
            String keyword, UUID premiseId, StoreStatus status, Set<UUID> allowedZoneIds) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String term = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("code")), term),
                        builder.like(builder.lower(root.get("name")), term)));
            }
            if (premiseId != null) {
                predicates.add(builder.equal(root.get("premise").get("id"), premiseId));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (allowedZoneIds != null && !allowedZoneIds.isEmpty()) {
                predicates.add(root.get("premise").get("zone").get("id").in(allowedZoneIds));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}

