package com.company.aneon.f10.repository;
import com.company.aneon.f10.entity.PhieuUuDai;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface VoucherRepository extends JpaRepository<PhieuUuDai, UUID> {
    boolean existsByCodeIgnoreCase(String code);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select v from PhieuUuDai v where v.id = :id")
    Optional<PhieuUuDai> findByIdForUpdate(@Param("id") UUID id);
}
