package com.lpu.application_service.controller;

import com.lpu.application_service.dto.ApplicationRequest;
import com.lpu.application_service.dto.ApplicationResponse;
import com.lpu.application_service.entity.LoanApplication;
import com.lpu.application_service.service.ApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('APPLICANT')")
    @PostMapping
    public ApplicationResponse create(@RequestBody ApplicationRequest request, @RequestHeader("X-User-Id") String userIdHeader) {
        Long userId = Long.valueOf(userIdHeader);
        return service.create(request, userId);
    }

    @GetMapping("/{id}/doc-test")
    public String testFeign(@PathVariable Long id) {
        return service.uploadDoc(id);
    }

    @PreAuthorize("hasRole('APPLICANT')")
    @PostMapping("/{id}/submit")
    public ApplicationResponse submit(@PathVariable Long id, @RequestHeader("X-User-Id") String userIdHeader) {
        Long userId = Long.valueOf(userIdHeader);
        return service.submit(id, userId);
    }

    @GetMapping("/{id}")
    public LoanApplication get(@PathVariable Long id) {
        return service.getById(id);
    }
}
