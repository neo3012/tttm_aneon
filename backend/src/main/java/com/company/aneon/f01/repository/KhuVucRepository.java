package com.company.aneon.f01.repository;

import com.company.aneon.f01.entity.KhuVuc;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KhuVucRepository extends JpaRepository<KhuVuc, UUID> {

    @EntityGraph(attributePaths = {"floor", "floor.building"})
    List<KhuVuc> findByActiveTrueOrderByNameAsc();

    @EntityGraph(attributePaths = {"floor", "floor.building"})
    Optional<KhuVuc> findByIdAndActiveTrue(UUID id);
}

