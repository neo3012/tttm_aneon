package com.company.aneon.f10.repository;

import com.company.aneon.f10.entity.HoatDongSuKien;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HoatDongSuKienRepository extends JpaRepository<HoatDongSuKien, UUID> {
    List<HoatDongSuKien> findByEventIdOrderByStartAtAsc(UUID eventId);

    @Query("""
            select (count(a) > 0) from HoatDongSuKien a
            where a.locationId = :locationId and a.startAt < :endAt and a.endAt > :startAt
            """)
    boolean hasScheduleConflict(
            @Param("locationId") UUID locationId,
            @Param("startAt") Instant startAt,
            @Param("endAt") Instant endAt);
}
