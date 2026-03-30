package com.lpu.application_service.service;

import com.lpu.application_service.dto.AdminReviewRequest;
import com.lpu.application_service.dto.ApplicationRequest;
import com.lpu.application_service.dto.ApplicationResponse;
import com.lpu.application_service.dto.LoanApplicationUpdateDTO;
import com.lpu.application_service.entity.LoanApplication;
import com.lpu.application_service.exception.CustomException;
import com.lpu.application_service.repository.LoanApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.lpu.application_service.messaging.ApplicationEventPublisher;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private LoanApplicationRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void createSavesDraftApplication() {
        ApplicationRequest request = new ApplicationRequest();
        request.setAmount(50000.0);
        request.setPurpose("Education");
        request.setTenure(24);

        when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> {
            LoanApplication application = invocation.getArgument(0);
            application.setId(1L);
            return application;
        });

        ApplicationResponse response = applicationService.create(request, 7L);

        ArgumentCaptor<LoanApplication> captor = ArgumentCaptor.forClass(LoanApplication.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getUserId()).isEqualTo(7L);
        assertThat(captor.getValue().getStatus()).isEqualTo("DRAFT");
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo("DRAFT");
    }

    @Test
    void submitMarksDraftAsSubmitted() {
        LoanApplication application = new LoanApplication();
        application.setId(11L);
        application.setUserId(5L);
        application.setStatus("DRAFT");

        when(repository.findById(11L)).thenReturn(Optional.of(application));
        when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationResponse response = applicationService.submit(11L, 5L);

        assertThat(application.getStatus()).isEqualTo("SUBMITTED");
        assertThat(application.getSubmittedAt()).isNotNull();
        assertThat(response.getStatus()).isEqualTo("SUBMITTED");
    }

    @Test
    void reviewRejectsApplicationOutsideReviewState() {
        LoanApplication application = new LoanApplication();
        application.setId(9L);
        application.setStatus("DRAFT");

        AdminReviewRequest request = new AdminReviewRequest();
        request.setStatus("APPROVED");

        when(repository.findById(9L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> applicationService.review(9L, request, "admin@finflow.com"))
                .isInstanceOf(CustomException.class)
                .hasMessage("Application is not in a state for review");
    }

    @Test
    void updateModifiesDraftApplication() {
        LoanApplication application = new LoanApplication();
        application.setId(100L);
        application.setUserId(10L);
        application.setStatus("DRAFT");

        LoanApplicationUpdateDTO dto = new LoanApplicationUpdateDTO();
        dto.setAmount(10000.0);
        dto.setPurpose("Holiday");

        when(repository.findById(100L)).thenReturn(Optional.of(application));

        ApplicationResponse response = applicationService.update(100L, dto, 10L);

        assertThat(application.getAmount()).isEqualTo(10000.0);
        assertThat(application.getPurpose()).isEqualTo("Holiday");
        assertThat(response.getStatus()).isEqualTo("DRAFT");
    }

    @Test
    void updateThrowsWhenNotDraft() {
        LoanApplication application = new LoanApplication();
        application.setId(101L);
        application.setUserId(11L);
        application.setStatus("SUBMITTED");

        when(repository.findById(101L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> applicationService.update(101L, new LoanApplicationUpdateDTO(), 11L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Only DRAFT applications can be updated");
    }

    @Test
    void cancelMarksAsCancelled() {
        LoanApplication application = new LoanApplication();
        application.setId(200L);
        application.setUserId(20L);
        application.setStatus("SUBMITTED");

        when(repository.findById(200L)).thenReturn(Optional.of(application));

        ApplicationResponse response = applicationService.cancel(200L, 20L);

        assertThat(application.getStatus()).isEqualTo("CANCELLED");
        assertThat(response.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void getByIdReturnsDetailResponse() {
        LoanApplication application = new LoanApplication();
        application.setId(300L);
        application.setUserId(30L);
        application.setStatus("APPROVED");

        when(repository.findById(300L)).thenReturn(Optional.of(application));

        var response = applicationService.getById(300L, 30L, "ROLE_APPLICANT");

        assertThat(response.getId()).isEqualTo(300L);
        assertThat(response.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void getByStatusReturnsList() {
        LoanApplication app1 = new LoanApplication();
        app1.setStatus("SUBMITTED");
        LoanApplication app2 = new LoanApplication();
        app2.setStatus("SUBMITTED");

        when(repository.findByStatus("SUBMITTED")).thenReturn(java.util.List.of(app1, app2));

        var list = applicationService.getByStatus("SUBMITTED");

        assertThat(list).hasSize(2);
    }

    @Test
    void submitChangesStatusFromDraft() {
        LoanApplication application = new LoanApplication();
        application.setId(500L);
        application.setUserId(50L);
        application.setStatus("DRAFT");

        when(repository.findById(500L)).thenReturn(Optional.of(application));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ApplicationResponse response = applicationService.submit(500L, 50L);

        assertThat(application.getStatus()).isEqualTo("SUBMITTED");
        assertThat(response.getStatus()).isEqualTo("SUBMITTED");
    }

    @Test
    void submitThrowsWhenAlreadySubmitted() {
        LoanApplication application = new LoanApplication();
        application.setId(501L);
        application.setUserId(51L);
        application.setStatus("SUBMITTED");

        when(repository.findById(501L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> applicationService.submit(501L, 51L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Only DRAFT applications can be submitted");
    }

}
