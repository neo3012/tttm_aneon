package com.company.aneon.f10.entity;
import com.company.aneon.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
@Getter @Setter @Entity @Table(name = "chi_phi_thuc_te") @SQLRestriction("archived_at IS NULL")
public class ChiPhiThucTe extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "event_id") private SuKien event;
    @Column(name = "supplier_name", length = 200) private String supplierName;
    @Column(name = "invoice_number", length = 100) private String invoiceNumber;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "incurred_at") private Instant incurredAt;
    @Column(name = "attachment_id", length = 36) private UUID attachmentId;
    @Column(length = 1000) private String description;
}
