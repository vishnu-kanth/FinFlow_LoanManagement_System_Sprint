package com.lpu.admin_service.service;

import com.lpu.admin_service.client.ApplicationClient;
import com.lpu.admin_service.dto.DecisionRequest;
import com.lpu.admin_service.dto.DecisionResponse;
import com.lpu.admin_service.entity.Decision;
import com.lpu.admin_service.exception.CustomException;
import com.lpu.admin_service.repository.DecisionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminService {

    private final DecisionRepository repo;
    private final ApplicationClient client;

    public AdminService(DecisionRepository repo, ApplicationClient client) {
        this.repo = repo;
        this.client = client;
    }


    public DecisionResponse makeDecision(Long appId, DecisionRequest request) {

        String decision = request.getDecision();
        if (decision == null || (!decision.equalsIgnoreCase("APPROVED") && !decision.equalsIgnoreCase("REJECTED"))) {
            throw new CustomException("Decision must be either APPROVED or REJECTED");
        }

        client.getApplication(appId);

        Decision d = new Decision();
        d.setApplicationId(appId);
        d.setDecision(decision.toUpperCase());
        d.setRemarks(request.getRemarks());
        d.setDecidedAt(LocalDateTime.now());

        repo.save(d);

        return new DecisionResponse(
                appId,
                decision.toUpperCase(),
                "Decision saved successfully"
        );
    }

}