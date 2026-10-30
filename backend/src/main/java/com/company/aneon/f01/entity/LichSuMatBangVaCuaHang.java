package com.company.aneon.f01.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lich_su_mat_bang_cua_hang")
public class LichSuMatBangVaCuaHang {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private UUID id;

    @Column(name = "target_type", nullable = false, length = 40)
    private String targetType;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "target_id", length = 36, nullable = false)
    private UUID targetId;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "old_value_json", columnDefinition = "json")
    private String oldValueJson;

    @Column(name = "new_value_json", columnDefinition = "json")
    private String newValueJson;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "changed_by", length = 36)
    private UUID changedBy;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "created_by", length = 36, updatable = false)
    private UUID createdBy;

    @PrePersist
    void initialize() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}

