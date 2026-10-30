package com.company.aneon.f10.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter @Entity @Table(name = "chuong_trinh_tang_phieu") @PrimaryKeyJoinColumn(name = "id")
public class ChuongTrinhTangPhieu extends ChuongTrinhKhuyenMai {
    @Column(name = "voucher_id", length = 36) private UUID voucherId;
    @Column(name = "total_to_issue") private Integer totalToIssue;
    @Column(name = "issued_count", nullable = false) private int issuedCount;
}
