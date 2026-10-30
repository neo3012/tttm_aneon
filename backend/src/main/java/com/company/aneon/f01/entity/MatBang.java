package com.company.aneon.f01.entity;

import com.company.aneon.common.entity.BaseEntity;
import com.company.aneon.f01.constant.PremiseStatus;
import com.company.aneon.f01.constant.PremiseType;
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
@Table(name = "mat_bang")
@SQLRestriction("archived_at IS NULL")
public class MatBang extends BaseEntity {

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zone_id", nullable = false)
    private KhuVuc zone;

    @Column(name = "total_area", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 40)
    private PremiseType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private PremiseStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private ViTri location;

    @Column(name = "description", length = 1000)
    private String description;
}

