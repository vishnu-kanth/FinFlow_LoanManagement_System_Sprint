package com.lpu.document_service.service;

import com.lpu.document_service.entity.Document;
import com.lpu.document_service.exception.CustomException;
import com.lpu.document_service.idempotency.IdempotencyService;
import com.lpu.document_service.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository repository;

    @Mock
    private IdempotencyService idempotencyService;

    @InjectMocks
    private DocumentService documentService;

    @TempDir
    Path tempDir;

    @Test
    void saveFileStoresDocumentMetadata() throws IOException {
        ReflectionTestUtils.setField(documentService, "uploadDir", tempDir.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "aadhaar.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(repository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(idempotencyService.executeIdempotent(any(), any(), eq(Document.class)))
                .thenAnswer(invocation -> invocation.<Supplier<Document>>getArgument(1).get());

        Document saved = documentService.saveFile(12L, file, null);

        assertThat(saved.getApplicationId()).isEqualTo(12L);
        assertThat(saved.getFileName()).isEqualTo("aadhaar.pdf");
        assertThat(saved.getStatus()).isEqualTo("PENDING");
        assertThat(Files.exists(tempDir.resolve("aadhaar.pdf"))).isTrue();
    }

    @Test
    void saveFileRejectsBlankFilename() {
        ReflectionTestUtils.setField(documentService, "uploadDir", tempDir.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "",
                "application/pdf",
                "content".getBytes()
        );

        when(idempotencyService.executeIdempotent(any(), any(), eq(Document.class)))
                .thenAnswer(invocation -> invocation.<Supplier<Document>>getArgument(1).get());

        assertThatThrownBy(() -> documentService.saveFile(12L, file, null))
                .isInstanceOf(CustomException.class)
                .hasMessage("File must have a valid name");
    }

    @Test
    void deleteRemovesStoredFileAndRepositoryRecord() throws IOException {
        Path storedFile = Files.createFile(tempDir.resolve("pancard.pdf"));
        Document document = new Document();
        document.setId(5L);
        document.setFilePath(storedFile.toString());

        when(repository.findById(5L)).thenReturn(Optional.of(document));

        String response = documentService.delete(5L);

        verify(repository).deleteById(5L);
        assertThat(Files.exists(storedFile)).isFalse();
        assertThat(response).isEqualTo("Document deleted successfully");
    }

    @Test
    void getByIdReturnsDocument() {
        Document doc = new Document();
        doc.setId(10L);
        when(repository.findById(10L)).thenReturn(Optional.of(doc));
        Document result = documentService.getById(10L);
        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void getByApplicationIdReturnsList() {
        Document doc1 = new Document();
        doc1.setApplicationId(100L);
        when(repository.findByApplicationId(100L)).thenReturn(java.util.List.of(doc1));
        var list = documentService.getByApplicationId(100L);
        assertThat(list).hasSize(1);
    }

    @Test
    void verifyUpdatesStatusAndTimestamp() {
        Document doc = new Document();
        doc.setId(1L);
        doc.setStatus("PENDING");
        when(repository.findById(1L)).thenReturn(Optional.of(doc));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        String result = documentService.verify(1L);

        assertThat(doc.getStatus()).isEqualTo("VERIFIED");
        assertThat(doc.getVerifiedAt()).isNotNull();
        assertThat(result).isEqualTo("Document verified successfully");
    }

    @Test
    void getVerifiedReturnsOnlyVerified() {
        Document doc = new Document();
        doc.setStatus("VERIFIED");
        when(repository.findByStatus("VERIFIED")).thenReturn(java.util.List.of(doc));
        var list = documentService.getVerified();
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getStatus()).isEqualTo("VERIFIED");
    }

    @Test
    void getByTypeReturnsMatchingDocuments() {
        Document doc = new Document();
        doc.setFileType("application/pdf");
        when(repository.findByFileType("application/pdf")).thenReturn(java.util.List.of(doc));
        var list = documentService.getByType("application/pdf");
        assertThat(list).hasSize(1);
    }

    @Test
    void getPendingReturnsOnlyPending() {
        Document doc = new Document();
        doc.setStatus("PENDING");
        when(repository.findByStatus("PENDING")).thenReturn(java.util.List.of(doc));
        var list = documentService.getPending();
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    void deleteThrowsWhenDocumentNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> documentService.delete(999L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Document not found");
    }

    @Test
    void getAllReturnsEmptyListWhenNoDocuments() {
        when(repository.findAll()).thenReturn(java.util.Collections.emptyList());
        var list = documentService.getAll();
        assertThat(list).isEmpty();
    }
}
