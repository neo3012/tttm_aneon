package com.company.aneon.f10.entity;

import com.company.aneon.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter @Setter @Entity @Table(name = "ma_qr_tham_gia") @SQLRestriction("archived_at IS NULL")
public class MaQrThamGia extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private SuKien event;
    @Column(name = "token_hash", nullable = false, unique = true, length = 128) private String tokenHash;
    @Column(name = "check_in_url", length = 512) private String checkInUrl;
    @Column(name = "image_storage_key", length = 300) private String imageStorageKey;
    @Column(name = "expires_at") private Instant expiresAt;
    @Column(nullable = false) private boolean active;
}
