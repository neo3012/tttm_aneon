package com.company.aneon.f01.repository;

import com.company.aneon.f01.entity.ToaNha;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToaNhaRepository extends JpaRepository<ToaNha, UUID> {
}

