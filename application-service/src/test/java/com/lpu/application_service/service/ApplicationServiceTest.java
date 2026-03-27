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
}
