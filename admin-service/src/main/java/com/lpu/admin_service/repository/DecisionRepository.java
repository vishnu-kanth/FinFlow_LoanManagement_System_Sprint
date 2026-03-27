package com.lpu.admin_service.repository;

import com.lpu.admin_service.entity.Decision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DecisionRepository extends JpaRepository<Decision, Long> {

    boolean existsByApplicationId(Long applicationId);

    Optional<Decision> findByApplicationId(Long applicationId);

    List<Decision> findByDecision(String decision);

    long countByDecision(String decision);

}
