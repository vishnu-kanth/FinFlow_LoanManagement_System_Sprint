package com.lpu.application_service.service;

import com.lpu.application_service.cilent.DocumentClient;
import com.lpu.application_service.dto.ApplicationRequest;
import com.lpu.application_service.dto.ApplicationResponse;
import com.lpu.application_service.entity.LoanApplication;
import com.lpu.application_service.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ApplicationService {

    private final LoanApplicationRepository repo;

    private final DocumentClient documentClient;

    public ApplicationService(LoanApplicationRepository repo, DocumentClient documentClient) {
        this.repo = repo;
        this.documentClient = documentClient;
    }

    public ApplicationResponse create(ApplicationRequest request) {

        LoanApplication app = new LoanApplication();
        app.setUserId(request.getUserId());
        app.setAmount(request.getAmount());
        app.setStatus("DRAFT");
        app.setCreatedAt(LocalDateTime.now());

        repo.save(app);

        return new ApplicationResponse(app.getId(), app.getStatus());
    }

    public String uploadDoc(Long id) {
        return "Call to Document Service ready";
    }


    public ApplicationResponse submit(Long id) {

        LoanApplication app = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.setStatus("SUBMITTED");
        repo.save(app);

        return new ApplicationResponse(app.getId(), app.getStatus());
    }

    public LoanApplication getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }
}