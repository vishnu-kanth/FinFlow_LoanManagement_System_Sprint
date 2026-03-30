package com.lpu.auth_service.controller;

import com.lpu.auth_service.dto.AuthRequest;
import com.lpu.auth_service.dto.AuthResponse;
import com.lpu.auth_service.dto.UserProfileResponse;
import com.lpu.auth_service.dto.UserUpdateRequest;
import com.lpu.auth_service.entity.User;
import com.lpu.auth_service.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public AuthResponse register(@RequestBody AuthRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return service.login(request);
    }
    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @PutMapping("/update")
    public AuthResponse update(Principal principal,
                               @RequestBody UserUpdateRequest request) {
        return service.update(principal.getName(), request);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<UserProfileResponse> getUsers() {
        return service.getAllUsers();
    }

    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @GetMapping("/profile")
    public UserProfileResponse profile(Principal principal) {
        return service.getProfile(principal.getName());
    }

    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @PostMapping("/logout")
    public String logout() {
        return service.logout();
    }

    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @PostMapping("/refresh-token")
    public AuthResponse refresh(Principal principal) {
        return service.refreshToken(principal.getName());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/{id}")
    public String delete(@PathVariable Long id) {
        return service.deleteUser(id);
    }

    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @GetMapping("/me")
    public UserProfileResponse me(Authentication authentication) {
        User user = service.getUserByEmail(authentication.getName());
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    @PostMapping("/promote-admin")
    public AuthResponse promoteAdmin(Principal principal, @RequestParam String adminSecret) {
        return service.promoteToAdmin(principal.getName(), adminSecret);
    }
}
