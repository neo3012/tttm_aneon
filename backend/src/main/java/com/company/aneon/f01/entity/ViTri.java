package com.company.aneon.f01.entity;

import com.company.aneon.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "vi_tri")
@SQLRestriction("archived_at IS NULL")
public class ViTri extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zone_id", nullable = false)
    private KhuVuc zone;

    @Column(name = "x", precision = 14, scale = 4)
    private BigDecimal x;

    @Column(name = "y", precision = 14, scale = 4)
    private BigDecimal y;

    @Column(name = "text_description", length = 500)
    private String textDescription;
}

