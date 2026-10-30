package com.company.aneon.f10.entity;
import com.company.aneon.common.entity.BaseEntity;
import com.company.aneon.f10.constant.VoucherIssueStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
@Getter @Setter @Entity @Table(name = "phieu_uu_dai_khach_hang") @SQLRestriction("archived_at IS NULL")
public class PhieuUuDaiKhachHang extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "voucher_id") private PhieuUuDai voucher;
    @Column(name = "customer_id", nullable = false, length = 36) private UUID customerId;
    @Column(name = "promotion_id", length = 36) private UUID promotionId;
    @Column(name = "issued_at") private Instant issuedAt;
    @Enumerated(EnumType.STRING) @Column(length = 40) private VoucherIssueStatus status;
    @Column(name = "redeemed_at") private Instant redeemedAt;
    @Column(name = "purchase_invoice_id", length = 36) private UUID purchaseInvoiceId;
    @Column(name = "issue_reference", length = 200) private String issueReference;
}
