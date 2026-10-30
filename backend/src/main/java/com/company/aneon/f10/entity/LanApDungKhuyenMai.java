package com.company.aneon.f10.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Getter @Setter @Entity @Table(name = "lan_ap_dung_khuyen_mai")
public class LanApDungKhuyenMai {
    @Id @JdbcTypeCode(SqlTypes.CHAR) @Column(length = 36) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "promotion_id")
    private ChuongTrinhKhuyenMai promotion;
    @Column(name = "customer_id", nullable = false, length = 36) private UUID customerId;
    @Column(name = "invoice_id", nullable = false, length = 36) private UUID invoiceId;
    @Column(name = "applied_at") private Instant appliedAt;
    @Column(name = "discount_amount", precision = 18, scale = 2) private BigDecimal discountAmount;
    @Column(name = "points_granted") private Long pointsGranted;
    @Column(name = "customer_voucher_id", length = 36) private UUID customerVoucherId;
    @Column(name = "idempotency_key", length = 120) private String idempotencyKey;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "created_by", length = 36, updatable = false) private UUID createdBy;
    @PrePersist void initialize() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); }
}
