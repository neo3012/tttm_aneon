package com.company.aneon.f10.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter @Setter @Entity @Table(name = "luot_tham_gia_su_kien")
public class LuotThamGiaSuKien {
    @Id @JdbcTypeCode(SqlTypes.CHAR) @Column(length = 36) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "event_id") private SuKien event;
    @Column(name = "customer_id", nullable = false, length = 36) private UUID customerId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "qr_code_id") private MaQrThamGia qrCode;
    @Column(name = "check_in_at") private Instant checkInAt;
    @Column(name = "scanning_device", length = 200) private String scanningDevice;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "created_by", length = 36, updatable = false) private UUID createdBy;

    @PrePersist
    void initialize() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
    }
}
