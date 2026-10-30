package com.company.aneon.f10.repository;
import com.company.aneon.f10.entity.PhieuUuDaiCuaHang;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VoucherStoreRepository extends JpaRepository<PhieuUuDaiCuaHang, UUID> {
    long countByVoucherId(UUID voucherId);
    boolean existsByVoucherIdAndStoreId(UUID voucherId, UUID storeId);
}
