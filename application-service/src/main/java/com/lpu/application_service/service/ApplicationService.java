package com.lpu.application_service.service;

import com.lpu.application_service.client.DocumentClient;
import com.lpu.application_service.dto.ApplicationRequest;
import com.lpu.application_service.dto.ApplicationResponse;
import com.lpu.application_service.entity.LoanApplication;
import com.lpu.application_service.exception.CustomException;
import com.lpu.application_service.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final LoanApplicationRepository repo;
    private final DocumentClient documentClient;

    // CREATE APPLICATION
    public ApplicationResponse create(ApplicationRequest request, Long userId) {

        if (userId == null || request.getAmount() == null) {
            throw new CustomException("UserId and Amount are required");
        }

        LoanApplication app = new LoanApplication();
        app.setUserId(userId);
        app.setAmount(request.getAmount());
        app.setPurpose(request.getPurpose());
        app.setTenure(request.getTenure());
        app.setEmploymentType(request.getEmploymentType());
        app.setMonthlyIncome(request.getMonthlyIncome());
        app.setPanNumber(request.getPanNumber());
        app.setStatus("DRAFT");
        app.setCreatedAt(LocalDateTime.now());

        LoanApplication saved = repo.save(app);

        return new ApplicationResponse(saved.getId(), saved.getStatus());
    }

    // STEP 3: FEIGN READY (placeholder → can extend later)
    public String uploadDoc(Long id) {

        LoanApplication app = repo.findById(id)
                .orElseThrow(() -> new CustomException("Application not found"));

        // Later we will call actual document service
        // documentClient.uploadDocument(id);

        return "Document upload triggered for Application ID: " + id;
    }

    // SUBMIT APPLICATION
    public ApplicationResponse submit(Long id, Long userId) {

        LoanApplication app = repo.findById(id)
                .orElseThrow(() -> new CustomException("Application not found"));

        if (!app.getUserId().equals(userId)) {
            throw new CustomException("You do not have permission to submit this application");
        }

        if (!"DRAFT".equals(app.getStatus())) {
            throw new CustomException("Only DRAFT applications can be submitted");
        }

        app.setStatus("SUBMITTED");
        repo.save(app);

        return new ApplicationResponse(app.getId(), app.getStatus());
    }

    // GET APPLICATION

    public LoanApplication getById(Long id) {

        return repo.findById(id)
                .orElseThrow(() -> new CustomException("Application not found"));
    }
}