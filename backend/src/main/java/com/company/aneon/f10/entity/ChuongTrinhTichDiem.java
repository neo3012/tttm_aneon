package com.company.aneon.f10.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter @Entity @Table(name = "chuong_trinh_tich_diem") @PrimaryKeyJoinColumn(name = "id")
public class ChuongTrinhTichDiem extends ChuongTrinhKhuyenMai {
    @Column(name = "reward_points") private Long rewardPoints;
    @Column(name = "conversion_amount", precision = 18, scale = 2) private BigDecimal conversionAmount;
    @Column(name = "points_per_conversion") private Integer pointsPerConversion;
}
