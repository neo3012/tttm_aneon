package com.company.aneon.f10.entity;
import com.company.aneon.common.entity.BaseEntity;
import com.company.aneon.f10.constant.VoucherDiscountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
@Getter @Setter @Entity @Table(name = "phieu_uu_dai") @SQLRestriction("archived_at IS NULL")
public class PhieuUuDai extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50) private String code;
    @Column(nullable = false, length = 200) private String name;
    @Enumerated(EnumType.STRING) @Column(name = "discount_type", length = 40) private VoucherDiscountType discountType;
    @Column(precision = 18, scale = 2) private BigDecimal value;
    @Column(name = "maximum_discount", precision = 18, scale = 2) private BigDecimal maximumDiscount;
    @Column(name = "minimum_invoice_amount", precision = 18, scale = 2) private BigDecimal minimumInvoiceAmount;
    @Column(name = "issued_limit") private Integer issuedLimit;
    @Column(name = "issued_count", nullable = false) private int issuedCount;
    @Column(name = "used_count", nullable = false) private int usedCount;
    @Column(name = "per_customer_limit") private Integer perCustomerLimit;
    @Column(name = "valid_from") private Instant validFrom;
    @Column(name = "expires_at") private Instant expiresAt;
    @Column(nullable = false) private boolean active;
}
