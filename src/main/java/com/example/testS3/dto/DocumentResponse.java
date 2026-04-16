package com.example.testS3.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para documento.
 * Retorna os dados do documento sem expor chaves S3 ou informações sensíveis.
 */
public record DocumentResponse(
    UUID id,
    String name,
    String contentType,
    Long fileSize,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

