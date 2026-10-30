package com.company.aneon.f10.entity;

import com.company.aneon.common.entity.BaseEntity;
import com.company.aneon.f10.constant.EventStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter @Setter @Entity @Table(name = "su_kien") @SQLRestriction("archived_at IS NULL")
public class SuKien extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50) private String code;
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 100) private String type;
    @Column(name = "location_id", length = 36) private UUID locationId;
    @Column(name = "start_at") private Instant startAt;
    @Column(name = "end_at") private Instant endAt;
    @Column(name = "estimated_budget", precision = 18, scale = 2) private BigDecimal estimatedBudget;
    @Column(name = "approved_budget", precision = 18, scale = 2) private BigDecimal approvedBudget;
    @Column(name = "target_attendees") private Integer targetAttendees;
    @Enumerated(EnumType.STRING) @Column(length = 40) private EventStatus status;
    @Column(name = "proposed_by", length = 36) private UUID proposedBy;
    @Column(name = "approved_by", length = 36) private UUID approvedBy;
    @Column(name = "approved_at") private Instant approvedAt;
    @Column(name = "rejection_reason", length = 500) private String rejectionReason;
    @Column(name = "public_visible", nullable = false) private boolean publicVisible;
}
