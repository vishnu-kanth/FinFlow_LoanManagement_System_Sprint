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
    private com.lpu.admin_service.messaging.DecisionEventPublisher eventPublisher;

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

    @Test
    void getByApplicationIdReturnsResponse() {
        com.lpu.admin_service.entity.Decision d = new com.lpu.admin_service.entity.Decision();
        d.setApplicationId(30L);
        d.setDecision("APPROVED");

        when(repository.findByApplicationId(30L)).thenReturn(java.util.Optional.of(d));

        DecisionResponse response = adminService.getByApplicationId(30L);

        assertThat(response.getApplicationId()).isEqualTo(30L);
        assertThat(response.getDecision()).isEqualTo("APPROVED");
    }

    @Test
    void getByApplicationIdThrowsWhenNotFound() {
        when(repository.findByApplicationId(40L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> adminService.getByApplicationId(40L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Decision not found");
    }

    @Test
    void getAllReturnsList() {
        com.lpu.admin_service.entity.Decision d1 = new com.lpu.admin_service.entity.Decision();
        d1.setApplicationId(1L);
        d1.setDecision("APPROVED");

        com.lpu.admin_service.entity.Decision d2 = new com.lpu.admin_service.entity.Decision();
        d2.setApplicationId(2L);
        d2.setDecision("REJECTED");

        when(repository.findAll()).thenReturn(java.util.List.of(d1, d2));

        java.util.List<DecisionResponse> list = adminService.getAll();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getApplicationId()).isEqualTo(1L);
        assertThat(list.get(1).getApplicationId()).isEqualTo(2L);
    }

    @Test
    void updateDecisionModifiesExistingRecord() {
        com.lpu.admin_service.entity.Decision d = new com.lpu.admin_service.entity.Decision();
        d.setId(100L);
        d.setApplicationId(50L);
        d.setDecision("APPROVED");

        DecisionRequest request = new DecisionRequest();
        request.setDecision("rejected");
        request.setRemarks("New info");

        when(repository.findById(100L)).thenReturn(java.util.Optional.of(d));

        DecisionResponse response = adminService.updateDecision(100L, request);

        assertThat(d.getDecision()).isEqualTo("REJECTED");
        assertThat(d.getRemarks()).isEqualTo("New info");
        assertThat(response.getDecision()).isEqualTo("REJECTED");
    }

    @Test
    void deleteDecisionRemovesRecord() {
        when(repository.existsById(200L)).thenReturn(true);

        String result = adminService.deleteDecision(200L);

        verify(repository).deleteById(200L);
        assertThat(result).isEqualTo("Decision deleted successfully");
    }

    @Test
    void deleteDecisionThrowsWhenNotFound() {
        when(repository.existsById(300L)).thenReturn(false);

        assertThatThrownBy(() -> adminService.deleteDecision(300L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Decision not found");
    }

    @Test
    void updateDecisionThrowsWhenNotFound() {
        when(repository.findById(400L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> adminService.updateDecision(400L, new DecisionRequest()))
                .isInstanceOf(CustomException.class)
                .hasMessage("Decision not found");
    }


    @Test
    void makeDecisionThrowsForInvalidDecisionType() {
        DecisionRequest request = new DecisionRequest();
        request.setDecision("MAYBE");

        assertThatThrownBy(() -> adminService.makeDecision(60L, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("Decision must be APPROVED or REJECTED");
    }
}
