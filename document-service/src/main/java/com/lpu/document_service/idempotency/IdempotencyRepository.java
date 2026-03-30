package com.lpu.document_service.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IdempotencyRepository extends JpaRepository<IdempotencyKey, String> {
    void deleteByCreatedAtBefore(LocalDateTime threshold);
}
