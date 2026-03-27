package com.lpu.application_service.service;

import com.lpu.application_service.dto.*;
import com.lpu.application_service.entity.LoanApplication;
import com.lpu.application_service.exception.CustomException;
import com.lpu.application_service.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final LoanApplicationRepository repo;

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

    // UPDATE APPLICATION
    public ApplicationResponse update(Long id, LoanApplicationUpdateDTO dto, Long userId) {
        LoanApplication app = repo.findById(id)
                .orElseThrow(() -> new CustomException("Application not found"));

        if (!app.getUserId().equals(userId)) {
            throw new CustomException("Permission denied");
        }

        if (!"DRAFT".equals(app.getStatus())) {
            throw new CustomException("Only DRAFT applications can be updated");
        }

        if (dto.getAmount() != null) app.setAmount(dto.getAmount());
        if (dto.getPurpose() != null) app.setPurpose(dto.getPurpose());
        if (dto.getTenure() != null) app.setTenure(dto.getTenure());
        if (dto.getEmploymentType() != null) app.setEmploymentType(dto.getEmploymentType());
        if (dto.getMonthlyIncome() != null) app.setMonthlyIncome(dto.getMonthlyIncome());
        if (dto.getPanNumber() != null) app.setPanNumber(dto.getPanNumber());

        repo.save(app);
        return new ApplicationResponse(app.getId(), app.getStatus());
    }

    // SUBMIT APPLICATION
    public ApplicationResponse submit(Long id, Long userId) {
        LoanApplication app = repo.findById(id)
                .orElseThrow(() -> new CustomException("Application not found"));

        if (!app.getUserId().equals(userId)) {
            throw new CustomException("Permission denied");
        }

        if (!"DRAFT".equals(app.getStatus())) {
            throw new CustomException("Only DRAFT applications can be submitted");
        }

        app.setStatus("SUBMITTED");
        app.setSubmittedAt(LocalDateTime.now());
        repo.save(app);

        return new ApplicationResponse(app.getId(), app.getStatus());
    }

    // CANCEL APPLICATION
    public ApplicationResponse cancel(Long id, Long userId) {
        LoanApplication app = repo.findById(id)
                .orElseThrow(() -> new CustomException("Application not found"));

        if (!app.getUserId().equals(userId)) {
            throw new CustomException("Permission denied");
        }

        if ("APPROVED".equals(app.getStatus()) || "REJECTED".equals(app.getStatus())) {
            throw new CustomException("Cannot cancel an application that is already processed");
        }

        app.setStatus("CANCELLED");
        repo.save(app);

        return new ApplicationResponse(app.getId(), app.getStatus());
    }

    // ADMIN REVIEW
    public ApplicationResponse review(Long id, AdminReviewRequest review, String adminEmail) {
        LoanApplication app = repo.findById(id)
                .orElseThrow(() -> new CustomException("Application not found"));

        if (!"SUBMITTED".equals(app.getStatus()) && !"NEEDS_INFO".equals(app.getStatus())) {
            throw new CustomException("Application is not in a state for review");
        }

        app.setStatus(review.getStatus());
        app.setAdminRemarks(review.getRemarks());
        app.setReviewedAt(LocalDateTime.now());
        app.setReviewedBy(adminEmail);
        repo.save(app);

        return new ApplicationResponse(app.getId(), app.getStatus());
    }

    // GETTERS
    public ApplicationDetailResponse getById(Long id, Long userId, String role) {
        LoanApplication app = repo.findById(id)
                .orElseThrow(() -> new CustomException("Application not found"));

        if ("ROLE_APPLICANT".equals(role) && !app.getUserId().equals(userId)) {
            throw new CustomException("Permission denied");
        }

        return mapToDetailResponse(app);
    }

    public List<ApplicationDetailResponse> getAll() {
        return repo.findAll().stream().map(this::mapToDetailResponse).collect(Collectors.toList());
    }

    public List<ApplicationDetailResponse> getByUserId(Long userId) {
        return repo.findByUserId(userId).stream().map(this::mapToDetailResponse).collect(Collectors.toList());
    }

    public List<ApplicationDetailResponse> getByStatus(String status) {
        return repo.findByStatus(status).stream().map(this::mapToDetailResponse).collect(Collectors.toList());
    }

    private ApplicationDetailResponse mapToDetailResponse(LoanApplication app) {
        return ApplicationDetailResponse.builder()
                .id(app.getId())
                .userId(app.getUserId())
                .amount(app.getAmount())
                .purpose(app.getPurpose())
                .tenure(app.getTenure())
                .employmentType(app.getEmploymentType())
                .monthlyIncome(app.getMonthlyIncome())
                .panNumber(app.getPanNumber())
                .status(app.getStatus())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .submittedAt(app.getSubmittedAt())
                .reviewedAt(app.getReviewedAt())
                .reviewedBy(app.getReviewedBy())
                .adminRemarks(app.getAdminRemarks())
                .build();
    }
}