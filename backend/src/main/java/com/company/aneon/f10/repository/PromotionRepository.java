package com.company.aneon.f10.repository;
import com.company.aneon.f10.entity.ChuongTrinhKhuyenMai;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface PromotionRepository extends JpaRepository<ChuongTrinhKhuyenMai, UUID>, JpaSpecificationExecutor<ChuongTrinhKhuyenMai> {
    boolean existsByCodeIgnoreCase(String code);
}
