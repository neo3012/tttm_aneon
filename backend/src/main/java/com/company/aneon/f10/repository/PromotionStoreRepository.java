package com.company.aneon.f10.repository;
import com.company.aneon.f10.entity.KhuyenMaiCuaHang;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PromotionStoreRepository extends JpaRepository<KhuyenMaiCuaHang, UUID> {
    long countByPromotionId(UUID promotionId);
    boolean existsByPromotionIdAndStoreId(UUID promotionId, UUID storeId);
}
