package com.company.aneon.f10.repository;

import com.company.aneon.f10.entity.MaQrThamGia;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaQrThamGiaRepository extends JpaRepository<MaQrThamGia, UUID> {
    Optional<MaQrThamGia> findByTokenHashAndActiveTrue(String tokenHash);
}
