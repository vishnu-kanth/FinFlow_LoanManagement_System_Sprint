package com.lpu.document_service.service;

import com.lpu.document_service.entity.Document;
import com.lpu.document_service.exception.CustomException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository repository;

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

        Document saved = documentService.saveFile(12L, file);

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

        assertThatThrownBy(() -> documentService.saveFile(12L, file))
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
}
