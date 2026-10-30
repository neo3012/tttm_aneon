package com.company.aneon.f01.repository;

import com.company.aneon.f01.entity.LichSuMatBangVaCuaHang;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LichSuMatBangVaCuaHangRepository
        extends JpaRepository<LichSuMatBangVaCuaHang, UUID> {

    Page<LichSuMatBangVaCuaHang> findByTargetTypeAndTargetIdAndCreatedAtBetween(
            String targetType, UUID targetId, Instant from, Instant to, Pageable pageable);
}

