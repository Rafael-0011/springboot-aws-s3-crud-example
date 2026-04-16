package com.example.testS3.controller;

import com.example.testS3.dto.CreateDocumentRequest;
import com.example.testS3.dto.DocumentResponse;
import com.example.testS3.dto.UpdateDocumentRequest;
import com.example.testS3.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Implementação da interface IDocumentController.
 * Controller REST para operações CRUD de documentos.
 * Endpoints para gerenciamento completo de documentos com upload/download no S3.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Documents", description = "API para gerenciamento de documentos com AWS S3")
@RequiredArgsConstructor
public class DocumentController implements IDocumentController {

    private final DocumentService documentService;


    @Override
    @PostMapping
    public ResponseEntity<DocumentResponse> createDocument(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestParam("name") String name,
            @Valid @RequestParam("contentType") String contentType) {

        log.info("Recebido POST para criar documento: {}", name);

        CreateDocumentRequest request = new CreateDocumentRequest(name, file.getOriginalFilename().getBytes(), contentType);
        DocumentResponse response = documentService.createDocument(file, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocument(
            @PathVariable UUID id) {

        log.info("Recebido GET para documento: {}", id);

        DocumentResponse response = documentService.getDocumentById(id);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<DocumentResponse>> getAllDocuments(Pageable pageable) {

        log.info("Recebido GET para listar documentos com paginação");

        Page<DocumentResponse> response = documentService.getAllDocuments(pageable);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/all")
    public ResponseEntity<List<DocumentResponse>> getAllDocumentsNoPagination() {

        log.info("Recebido GET para listar todos os documentos");

        List<DocumentResponse> response = documentService.getAllDocuments();

        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDocumentRequest request) {

        log.info("Recebido PUT para atualizar documento: {}", id);

        DocumentResponse response = documentService.updateDocument(id, request);

        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{id}/file")
    public ResponseEntity<DocumentResponse> updateDocumentFile(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file) {

        log.info("Recebido PATCH para atualizar arquivo do documento: {}", id);

        DocumentResponse response = documentService.updateDocumentFile(id, file);

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {

        log.info("Recebido DELETE para documento: {}", id);

        documentService.deleteDocument(id);

        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable UUID id) {

        log.info("Recebido GET para fazer download do documento: {}", id);

        DocumentResponse documentInfo = documentService.getDocumentById(id);
        byte[] fileContent = documentService.downloadDocumentFile(id);

        return ResponseEntity.ok()
            .header("Content-Disposition",
                "attachment; filename=\"" + documentInfo.name() + "\"")
            .header("Content-Type", documentInfo.contentType())
            .body(fileContent);
    }
}

