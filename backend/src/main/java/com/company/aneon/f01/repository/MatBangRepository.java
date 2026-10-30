package com.company.aneon.f01.repository;

import com.company.aneon.f01.entity.MatBang;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatBangRepository extends JpaRepository<MatBang, UUID>, JpaSpecificationExecutor<MatBang> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);

    @Override
    @EntityGraph(attributePaths = {"zone", "zone.floor", "zone.floor.building", "location"})
    Optional<MatBang> findById(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MatBang m where m.id = :id")
    Optional<MatBang> findByIdForUpdate(@Param("id") UUID id);
}

