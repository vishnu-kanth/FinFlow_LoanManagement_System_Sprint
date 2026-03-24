package com.lpu.application_service.controller;

import com.lpu.application_service.dto.ApplicationRequest;
import com.lpu.application_service.dto.ApplicationResponse;
import com.lpu.application_service.entity.LoanApplication;
import com.lpu.application_service.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ApplicationResponse create(@RequestBody ApplicationRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}/doc-test")
    public String testFeign(@PathVariable Long id) {
        return service.uploadDoc(id);
    }

    @PostMapping("/{id}/submit")
    public ApplicationResponse submit(@PathVariable Long id) {
        return service.submit(id);
    }

    @GetMapping("/{id}")
    public LoanApplication get(@PathVariable Long id) {
        return service.getById(id);
    }
}
