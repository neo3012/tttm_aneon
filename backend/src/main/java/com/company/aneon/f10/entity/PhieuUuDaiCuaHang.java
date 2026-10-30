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
@Getter @Setter @Entity @Table(name = "phieu_uu_dai_cua_hang")
public class PhieuUuDaiCuaHang {
    @Id @JdbcTypeCode(SqlTypes.CHAR) @Column(length = 36) private UUID id;
    @Column(name = "voucher_id", nullable = false, length = 36) private UUID voucherId;
    @Column(name = "store_id", nullable = false, length = 36) private UUID storeId;
    @PrePersist void initialize() { if (id == null) id = UUID.randomUUID(); }
}
