package com.company.aneon.f10.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter @Entity @Table(name = "chuong_trinh_giam_gia") @PrimaryKeyJoinColumn(name = "id")
public class ChuongTrinhGiamGia extends ChuongTrinhKhuyenMai {
    @Column(name = "discount_rate", precision = 9, scale = 4) private BigDecimal discountRate;
    @Column(name = "maximum_discount", precision = 18, scale = 2) private BigDecimal maximumDiscount;
}
