package com.company.aneon.f10.repository;
import com.company.aneon.f10.entity.F10Idempotency;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface F10IdempotencyRepository extends JpaRepository<F10Idempotency, UUID> {
    Optional<F10Idempotency> findByOperationNameAndIdempotencyKey(String operationName, String idempotencyKey);
}
