package com.company.aneon.f01.entity;

import com.company.aneon.common.entity.BaseEntity;
import com.company.aneon.f01.constant.StoreStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cua_hang")
@SQLRestriction("archived_at IS NULL")
public class CuaHangSap extends BaseEntity {

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "premise_id", nullable = false)
    private MatBang premise;

    @Column(name = "area", nullable = false, precision = 14, scale = 2)
    private BigDecimal area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private NganhHang category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private ViTri location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private StoreStatus status;

    @Column(name = "operating_hours", length = 200)
    private String operatingHours;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "public_visible", nullable = false)
    private boolean publicVisible;
}

