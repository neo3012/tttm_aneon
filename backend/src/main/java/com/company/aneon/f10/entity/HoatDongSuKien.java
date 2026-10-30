package com.company.aneon.f10.entity;

import com.company.aneon.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter @Setter @Entity @Table(name = "hoat_dong_su_kien") @SQLRestriction("archived_at IS NULL")
public class HoatDongSuKien extends BaseEntity {
    @Column(nullable = false, length = 200) private String name;
    @Column(name = "start_at") private Instant startAt;
    @Column(name = "end_at") private Instant endAt;
    @Column(name = "location_id", length = 36) private UUID locationId;
    @Column(name = "person_in_charge", length = 36) private UUID personInCharge;
    @Column(length = 1000) private String description;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private SuKien event;
}
