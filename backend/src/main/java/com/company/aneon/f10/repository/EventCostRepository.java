package com.company.aneon.f10.repository;
import com.company.aneon.f10.entity.ChiPhiThucTe;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface EventCostRepository extends JpaRepository<ChiPhiThucTe, UUID> {
    List<ChiPhiThucTe> findByEventIdOrderByIncurredAtDesc(UUID eventId);
    @Query("select coalesce(sum(c.amount), 0) from ChiPhiThucTe c where c.event.id = :eventId")
    BigDecimal totalByEventId(@Param("eventId") UUID eventId);
}
