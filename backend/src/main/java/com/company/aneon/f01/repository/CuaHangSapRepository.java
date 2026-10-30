package com.company.aneon.f01.repository;

import com.company.aneon.f01.entity.CuaHangSap;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CuaHangSapRepository
        extends JpaRepository<CuaHangSap, UUID>, JpaSpecificationExecutor<CuaHangSap> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);

    @Query("select coalesce(sum(s.area), 0) from CuaHangSap s where s.premise.id = :premiseId and s.id <> :excludeId")
    BigDecimal sumAreaByPremiseExcluding(@Param("premiseId") UUID premiseId, @Param("excludeId") UUID excludeId);

    @Query("select coalesce(sum(s.area), 0) from CuaHangSap s where s.premise.id = :premiseId")
    BigDecimal sumAreaByPremise(@Param("premiseId") UUID premiseId);

    @Override
    @EntityGraph(attributePaths = {"premise", "premise.zone", "category", "location"})
    Optional<CuaHangSap> findById(UUID id);
}

