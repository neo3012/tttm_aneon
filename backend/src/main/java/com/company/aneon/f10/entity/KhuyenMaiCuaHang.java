package com.company.aneon.f10.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Getter @Setter @Entity @Table(name = "khuyen_mai_cua_hang")
public class KhuyenMaiCuaHang {
    @Id @JdbcTypeCode(SqlTypes.CHAR) @Column(length = 36) private UUID id;
    @Column(name = "promotion_id", nullable = false, length = 36) private UUID promotionId;
    @Column(name = "store_id", nullable = false, length = 36) private UUID storeId;
    @PrePersist void initialize() { if (id == null) id = UUID.randomUUID(); }
}
