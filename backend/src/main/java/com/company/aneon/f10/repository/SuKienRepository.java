package com.company.aneon.f10.repository;

import com.company.aneon.f10.entity.SuKien;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SuKienRepository extends JpaRepository<SuKien, UUID>, JpaSpecificationExecutor<SuKien> {
    boolean existsByCodeIgnoreCase(String code);
}
