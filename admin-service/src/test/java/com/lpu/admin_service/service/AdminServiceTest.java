package com.lpu.admin_service.service;

import com.lpu.admin_service.client.ApplicationClient;
import com.lpu.admin_service.dto.DecisionRequest;
import com.lpu.admin_service.dto.DecisionResponse;
import com.lpu.admin_service.exception.CustomException;
import com.lpu.admin_service.repository.DecisionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private DecisionRepository repository;

    @Mock
    private ApplicationClient applicationClient;

    @InjectMocks
    private AdminService adminService;

    @Test
    void makeDecisionSavesUppercaseDecision() {
        DecisionRequest request = new DecisionRequest();
        request.setDecision("approved");
        request.setRemarks("Eligible");

        when(applicationClient.getApplication(10L)).thenReturn(new Object());
        when(repository.existsByApplicationId(10L)).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        DecisionResponse response = adminService.makeDecision(10L, request);

        ArgumentCaptor<com.lpu.admin_service.entity.Decision> captor =
                ArgumentCaptor.forClass(com.lpu.admin_service.entity.Decision.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getApplicationId()).isEqualTo(10L);
        assertThat(captor.getValue().getDecision()).isEqualTo("APPROVED");
        assertThat(response.getApplicationId()).isEqualTo(10L);
        assertThat(response.getDecision()).isEqualTo("APPROVED");
    }

    @Test
    void makeDecisionThrowsWhenDecisionAlreadyExists() {
        DecisionRequest request = new DecisionRequest();
        request.setDecision("REJECTED");

        when(applicationClient.getApplication(20L)).thenReturn(new Object());
        when(repository.existsByApplicationId(20L)).thenReturn(true);

        assertThatThrownBy(() -> adminService.makeDecision(20L, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("Decision already exists for this application");

        verify(repository, never()).save(any());
    }
}
