package com.lpu.admin_service.service;

import com.lpu.admin_service.client.ApplicationClient;
import com.lpu.admin_service.dto.DecisionRequest;
import com.lpu.admin_service.dto.DecisionResponse;
import com.lpu.admin_service.entity.Decision;
import com.lpu.admin_service.exception.CustomException;
import com.lpu.admin_service.messaging.DecisionEventPublisher;
import com.lpu.admin_service.repository.DecisionRepository;
import com.lpu.admin_service.idempotency.IdempotencyService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final DecisionRepository repo;
    private final ApplicationClient client;
    private final DecisionEventPublisher decisionEventPublisher;
    private final IdempotencyService idempotencyService;

    public AdminService(DecisionRepository repo, ApplicationClient client, DecisionEventPublisher decisionEventPublisher, IdempotencyService idempotencyService) {
        this.repo = repo;
        this.client = client;
        this.decisionEventPublisher = decisionEventPublisher;
        this.idempotencyService = idempotencyService;
    }

    public DecisionResponse makeDecision(Long appId, DecisionRequest request, String idempotencyKey) {
        return idempotencyService.executeIdempotent(idempotencyKey, () -> {
            validateDecision(request.getDecision());

            // Check application exists
            client.getApplication(appId);

            // Prevent duplicate decision
            if (repo.existsByApplicationId(appId)) {
                throw new CustomException("Decision already exists for this application");
            }

            Decision d = new Decision();
            d.setApplicationId(appId);
            d.setDecision(request.getDecision().toUpperCase());
            d.setRemarks(request.getRemarks());
            d.setDecidedAt(LocalDateTime.now());

            repo.save(d);

            // Publish async event to RabbitMQ
            decisionEventPublisher.publishDecisionMade(
                    appId, d.getDecision(), d.getRemarks(), "admin"
            );

            return mapToResponse(d);
        }, DecisionResponse.class);
    }

    public DecisionResponse makeDecision(DecisionRequest request) {

        if (request.getApplicationId() == null) {
            throw new CustomException("Application ID is required");
        }

        return makeDecision(request.getApplicationId(), request, null);
    }
    public DecisionResponse getByApplicationId(Long appId) {

        Decision d = repo.findByApplicationId(appId)
                .orElseThrow(() -> new CustomException("Decision not found"));

        return mapToResponse(d);
    }

    public List<DecisionResponse> getAll() {
        return repo.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }


    public List<DecisionResponse> getByStatus(String status) {
        return repo.findByDecision(status.toUpperCase()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DecisionResponse updateDecision(Long id, DecisionRequest request) {

        Decision d = repo.findById(id)
                .orElseThrow(() -> new CustomException("Decision not found"));

        if (request.getDecision() != null) {
            validateDecision(request.getDecision());
            d.setDecision(request.getDecision().toUpperCase());
        }

        if (request.getRemarks() != null) {
            d.setRemarks(request.getRemarks());
        }

        repo.save(d);

        return mapToResponse(d);
    }


    public String deleteDecision(Long id) {

        if (!repo.existsById(id)) {
            throw new CustomException("Decision not found");
        }

        repo.deleteById(id);
        return "Decision deleted successfully";
    }


    public Object getApplication(Long id) {
        return client.getApplication(id);
    }


    public Object getAllApplications() {
        return client.getAllApplications();
    }


    public Object getApplicationsByStatus(String status) {
        return client.getByStatus(status);
    }


    public Map<String, Long> getStats() {

        Map<String, Long> stats = new HashMap<>();

        stats.put("total", repo.count());
        stats.put("approved", repo.countByDecision("APPROVED"));
        stats.put("rejected", repo.countByDecision("REJECTED"));

        return stats;
    }


    private void validateDecision(String decision) {
        if (decision == null ||
                (!decision.equalsIgnoreCase("APPROVED")
                        && !decision.equalsIgnoreCase("REJECTED"))) {

            throw new CustomException("Decision must be APPROVED or REJECTED");
        }
    }


    private DecisionResponse mapToResponse(Decision d) {
        return new DecisionResponse(
                d.getApplicationId(),
                d.getDecision(),
                "Success"
        );
    }
}