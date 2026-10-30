package com.company.aneon.f10.repository;

import com.company.aneon.f10.entity.LuotThamGiaSuKien;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LuotThamGiaSuKienRepository extends JpaRepository<LuotThamGiaSuKien, UUID> {
    boolean existsByEventIdAndCustomerId(UUID eventId, UUID customerId);
}
