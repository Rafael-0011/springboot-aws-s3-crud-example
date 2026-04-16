package com.example.testS3.service;

import com.example.testS3.domain.Document;
import com.example.testS3.dto.CreateDocumentRequest;
import com.example.testS3.dto.DocumentResponse;
import com.example.testS3.dto.UpdateDocumentRequest;
import com.example.testS3.exception.ResourceNotFoundException;
import com.example.testS3.exception.ValidationException;
import com.example.testS3.mapper.DocumentMapper;
import com.example.testS3.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para DocumentService.
 * Testa lógica de negócio sem dependências externas (mocks).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentService Tests")
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private DocumentMapper documentMapper;

    @InjectMocks
    private DocumentService documentService;

    private Document mockDocument;
    private DocumentResponse mockDocumentResponse;
    private MultipartFile mockFile;
    private java.util.UUID mockId;

    @BeforeEach
    void setUp() {
        mockId = java.util.UUID.randomUUID();
        mockDocument = Document.builder()
            .id(mockId)
            .name("Test Document")
            .s3Key("documents/uuid/test.pdf")
            .fileSize(1024L)
            .contentType("application/pdf")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        mockDocumentResponse = new DocumentResponse(
            mockId,
            "Test Document",
            "test.pdf",
            1024L,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        mockFile = new MockMultipartFile(
            "file",
            "test.pdf",
            "application/pdf",
            "test content".getBytes()
        );
    }

    @Test
    @DisplayName("Should create document successfully")
    void testCreateDocumentSuccess() {
        // Arrange
        CreateDocumentRequest request = new CreateDocumentRequest("Test Doc", "test content".getBytes(), "Test Description");
        when(s3Service.uploadFile(any(byte[].class), anyString(), anyString()))
            .thenReturn("documents/uuid/test.pdf");
        when(documentRepository.save(any(Document.class))).thenReturn(mockDocument);
        when(documentMapper.toResponse(mockDocument)).thenReturn(mockDocumentResponse);

        // Act
        DocumentResponse response = documentService.createDocument(mockFile, request);

        // Assert
        assertNotNull(response);
        assertEquals("Test Document", response.name());
        verify(s3Service, times(1)).uploadFile(any(byte[].class), anyString(), anyString());
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    @DisplayName("Should throw ValidationException for empty file")
    void testCreateDocumentWithEmptyFile() {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);
        CreateDocumentRequest request = new CreateDocumentRequest("Test Doc", new byte[0], "Test Description");

        // Act & Assert
        assertThrows(ValidationException.class, () ->
            documentService.createDocument(emptyFile, request)
        );
    }

    @Test
    @DisplayName("Should get document by ID successfully")
    void testGetDocumentByIdSuccess() {
        // Arrange
        when(documentRepository.findById(mockId)).thenReturn(Optional.of(mockDocument));
        when(documentMapper.toResponse(mockDocument)).thenReturn(mockDocumentResponse);

        // Act
        DocumentResponse response = documentService.getDocumentById(mockId);

        // Assert
        assertNotNull(response);
        assertEquals("Test Document", response.name());
        verify(documentRepository, times(1)).findById(mockId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when document not found")
    void testGetDocumentByIdNotFound() {
        // Arrange
        java.util.UUID notFoundId = java.util.UUID.randomUUID();
        when(documentRepository.findById(notFoundId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
            documentService.getDocumentById(notFoundId)
        );
    }

    @Test
    @DisplayName("Should update document successfully")
    void testUpdateDocumentSuccess() {
        // Arrange
        UpdateDocumentRequest request = new UpdateDocumentRequest("Updated Name");
        when(documentRepository.findById(mockId)).thenReturn(Optional.of(mockDocument));
        when(documentRepository.save(any(Document.class))).thenReturn(mockDocument);
        when(documentMapper.toResponse(mockDocument)).thenReturn(mockDocumentResponse);

        // Act
        DocumentResponse response = documentService.updateDocument(mockId, request);

        // Assert
        assertNotNull(response);
        verify(documentRepository, times(1)).findById(mockId);
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    @DisplayName("Should delete document successfully")
    void testDeleteDocumentSuccess() {
        // Arrange
        when(documentRepository.findById(mockId)).thenReturn(Optional.of(mockDocument));
        doNothing().when(s3Service).deleteFile(anyString());
        doNothing().when(documentRepository).deleteById(mockId);

        // Act
        documentService.deleteDocument(mockId);

        // Assert
        verify(documentRepository, times(1)).findById(mockId);
        verify(s3Service, times(1)).deleteFile("documents/uuid/test.pdf");
        verify(documentRepository, times(1)).deleteById(mockId);
    }

    @Test
    @DisplayName("Should download document file successfully")
    void testDownloadDocumentFileSuccess() {
        // Arrange
        byte[] fileContent = "test content".getBytes();
        when(documentRepository.findById(mockId)).thenReturn(Optional.of(mockDocument));
        when(s3Service.downloadFile("documents/uuid/test.pdf")).thenReturn(fileContent);

        // Act
        byte[] content = documentService.downloadDocumentFile(mockId);

        // Assert
        assertNotNull(content);
        assertEquals(fileContent.length, content.length);
        verify(documentRepository, times(1)).findById(mockId);
        verify(s3Service, times(1)).downloadFile("documents/uuid/test.pdf");
    }
}
