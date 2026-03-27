package com.lpu.admin_service.controller;

import com.lpu.admin_service.dto.DecisionRequest;
import com.lpu.admin_service.dto.DecisionResponse;
import com.lpu.admin_service.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    // DECIDE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/applications/{id}/decision")
    public DecisionResponse decideById(
            @PathVariable Long id,
            @RequestBody DecisionRequest request) {

        return service.makeDecision(id, request);
    }

    //  DECIDE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/decision")
    public DecisionResponse decide(@RequestBody DecisionRequest request) {
        return service.makeDecision(request);
    }

    //  GET DECISION BY APPLICATION ID

    @GetMapping("/decision/application/{appId}")
    public DecisionResponse getByApplication(@PathVariable Long appId) {
        return service.getByApplicationId(appId);
    }

    //  GET ALL DECISIONS

    @GetMapping("/decisions")
    public List<DecisionResponse> getAll() {
        return service.getAll();
    }

    // GET DECISIONS BY STATUS
    @GetMapping("/decisions/status/{status}")
    public List<DecisionResponse> getByStatus(@PathVariable String status) {
        return service.getByStatus(status);
    }

    // UPDATE DECISION
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/decision/{id}")
    public DecisionResponse update(
            @PathVariable Long id,
            @RequestBody DecisionRequest request) {

        return service.updateDecision(id, request);
    }

    // DELETE DECISION
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/decision/{id}")
    public String delete(@PathVariable Long id) {
        return service.deleteDecision(id);
    }

    // GET APPLICATION BY ID (Feign)
    @GetMapping("/application/{id}")
    public Object getApplication(@PathVariable Long id) {
        return service.getApplication(id);
    }

    // GET ALL APPLICATIONS (Feign)
    @GetMapping("/applications")
    public Object getAllApplications() {
        return service.getAllApplications();
    }

    // GET APPLICATIONS BY STATUS (Feign)
    @GetMapping("/applications/status/{status}")
    public Object getApplicationsByStatus(@PathVariable String status) {
        return service.getApplicationsByStatus(status);
    }

    // ADMIN STATS
    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return service.getStats();
    }
}