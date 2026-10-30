package com.company.aneon.f10.entity;

import com.company.aneon.common.entity.BaseEntity;
import com.company.aneon.f10.constant.PromotionStatus;
import com.company.aneon.f10.constant.PromotionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter @Setter @Entity @Table(name = "chuong_trinh_khuyen_mai")
@Inheritance(strategy = InheritanceType.JOINED) @SQLRestriction("archived_at IS NULL")
public abstract class ChuongTrinhKhuyenMai extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50) private String code;
    @Column(nullable = false, length = 200) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40) private PromotionType type;
    @Column(name = "start_at") private Instant startAt;
    @Column(name = "end_at") private Instant endAt;
    @Column(name = "minimum_invoice_amount", precision = 18, scale = 2) private BigDecimal minimumInvoiceAmount;
    @Column(name = "target_tiers", columnDefinition = "json") private String targetTiers;
    @Enumerated(EnumType.STRING) @Column(length = 40) private PromotionStatus status;
    @Column(columnDefinition = "text") private String terms;
    @Column(name = "public_visible", nullable = false) private boolean publicVisible;
}
