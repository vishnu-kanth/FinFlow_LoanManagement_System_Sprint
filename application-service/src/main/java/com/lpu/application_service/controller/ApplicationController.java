package com.lpu.application_service.controller;

import com.lpu.application_service.dto.*;
import com.lpu.application_service.entity.LoanApplication;
import com.lpu.application_service.service.ApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    // 1. CREATE APPLICATION (APPLICANT)
    @PreAuthorize("hasRole('APPLICANT')")
    @PostMapping
    public ApplicationResponse create(@RequestBody ApplicationRequest request, @RequestHeader("X-User-Id") String userIdHeader) {
        Long userId = Long.valueOf(userIdHeader);
        return service.create(request, userId);
    }

    // 2. UPDATE APPLICATION (APPLICANT)
    @PreAuthorize("hasRole('APPLICANT')")
    @PutMapping("/{id}")
    public ApplicationResponse update(@PathVariable Long id, @RequestBody LoanApplicationUpdateDTO dto, @RequestHeader("X-User-Id") String userIdHeader) {
        Long userId = Long.valueOf(userIdHeader);
        return service.update(id, dto, userId);
    }

    // 3. SUBMIT APPLICATION (APPLICANT)
    @PreAuthorize("hasRole('APPLICANT')")
    @PatchMapping("/{id}/submit")
    public ApplicationResponse submit(@PathVariable Long id, @RequestHeader("X-User-Id") String userIdHeader) {
        Long userId = Long.valueOf(userIdHeader);
        return service.submit(id, userId);
    }

    // 4. CANCEL APPLICATION (APPLICANT)
    @PreAuthorize("hasRole('APPLICANT')")
    @PatchMapping("/{id}/cancel")
    public ApplicationResponse cancel(@PathVariable Long id, @RequestHeader("X-User-Id") String userIdHeader) {
        Long userId = Long.valueOf(userIdHeader);
        return service.cancel(id, userId);
    }

    // 5. GET BY ID (APPLICANT - own, ADMIN - all)
    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @GetMapping("/{id}")
    public ApplicationDetailResponse get(@PathVariable Long id, @RequestHeader("X-User-Id") String userIdHeader, Authentication auth) {
        Long userId = Long.valueOf(userIdHeader);
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
        return service.getById(id, userId, role);
    }

    // 6. GET ALL (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ApplicationDetailResponse> getAll() {
        return service.getAll();
    }

    // 7. GET BY USER (APPLICANT - self, ADMIN - all)
    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @GetMapping("/user/{userId}")
    public List<ApplicationDetailResponse> getByUser(@PathVariable Long userId, @RequestHeader("X-User-Id") String userIdHeader, Authentication auth) {
        Long requesterId = Long.valueOf(userIdHeader);
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
        
        if ("ROLE_APPLICANT".equals(role) && !requesterId.equals(userId)) {
            throw new RuntimeException("Permission denied"); // Or custom exception
        }
        return service.getByUserId(userId);
    }

    // 8. GET BY STATUS (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public List<ApplicationDetailResponse> getByStatus(@PathVariable String status) {
        return service.getByStatus(status);
    }

    // 9. APPROVE (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/approve")
    public ApplicationResponse approve(@PathVariable Long id, @RequestBody AdminReviewRequest review, Authentication auth) {
        review.setStatus("APPROVED");
        return service.review(id, review, auth.getName());
    }

    // 10. REJECT (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reject")
    public ApplicationResponse reject(@PathVariable Long id, @RequestBody AdminReviewRequest review, Authentication auth) {
        review.setStatus("REJECTED");
        return service.review(id, review, auth.getName());
    }

    // 11. REQUEST INFO (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/request-info")
    public ApplicationResponse requestInfo(@PathVariable Long id, @RequestBody AdminReviewRequest review, Authentication auth) {
        review.setStatus("NEEDS_INFO");
        return service.review(id, review, auth.getName());
    }
}
