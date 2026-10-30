package com.company.aneon.f10.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Getter @Setter @Entity
@Table(name = "f10_idempotency", uniqueConstraints = @UniqueConstraint(columnNames = {"operation_name", "idempotency_key"}))
public class F10Idempotency {
    @Id @JdbcTypeCode(SqlTypes.CHAR) @Column(length = 36) private UUID id;
    @Column(name = "operation_name", nullable = false, length = 40) private String operationName;
    @Column(name = "idempotency_key", nullable = false, length = 120) private String idempotencyKey;
    @Column(name = "result_id", nullable = false, length = 36) private UUID resultId;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void initialize() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); }
}
