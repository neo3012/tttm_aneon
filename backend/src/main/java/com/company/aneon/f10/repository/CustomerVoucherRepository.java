package com.company.aneon.f10.repository;
import com.company.aneon.f10.entity.PhieuUuDaiKhachHang;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerVoucherRepository extends JpaRepository<PhieuUuDaiKhachHang, UUID> {
    long countByVoucherIdAndCustomerId(UUID voucherId, UUID customerId);
    Optional<PhieuUuDaiKhachHang> findByIdAndCustomerId(UUID id, UUID customerId);
}
