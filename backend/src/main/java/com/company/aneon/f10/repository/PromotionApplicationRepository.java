package com.company.aneon.f10.repository;
import com.company.aneon.f10.entity.LanApDungKhuyenMai;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PromotionApplicationRepository extends JpaRepository<LanApDungKhuyenMai, UUID> {
    boolean existsByPromotionIdAndInvoiceId(UUID promotionId, UUID invoiceId);
}
