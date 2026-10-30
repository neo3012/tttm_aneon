package com.company.aneon.f01.repository;

import com.company.aneon.f01.entity.NganhHang;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NganhHangRepository extends JpaRepository<NganhHang, UUID> {

    List<NganhHang> findAllByOrderByNameAsc();
}

