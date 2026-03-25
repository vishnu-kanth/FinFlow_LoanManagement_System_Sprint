package com.lpu.admin_service.service;

import com.lpu.admin_service.client.ApplicationClient;
import com.lpu.admin_service.dto.DecisionRequest;
import com.lpu.admin_service.dto.DecisionResponse;
import com.lpu.admin_service.entity.Decision;
import com.lpu.admin_service.repository.DecisionRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final DecisionRepository repo;
    private final ApplicationClient client;

    public AdminService(DecisionRepository repo, ApplicationClient client) {
        this.repo = repo;
        this.client = client;
    }


    public DecisionResponse makeDecision(Long appId, DecisionRequest request) {

        client.getApplication(appId);

        Decision d = new Decision();
        d.setApplicationId(appId);
        d.setDecision(request.getDecision());
        d.setRemarks(request.getRemarks());

        repo.save(d);

        return new DecisionResponse(
                appId,
                request.getDecision(),
                "Decision saved successfully"
        );
    }

}