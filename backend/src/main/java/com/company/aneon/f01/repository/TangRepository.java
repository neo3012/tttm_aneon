package com.company.aneon.f01.repository;

import com.company.aneon.f01.entity.Tang;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TangRepository extends JpaRepository<Tang, UUID> {

    @EntityGraph(attributePaths = "building")
    List<Tang> findByActiveTrueOrderByLevelAsc();
}

