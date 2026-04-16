package com.example.testS3.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para atualizar um documento existente.
 */
public record UpdateDocumentRequest(
    @NotBlank(message = "Nome do documento é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    String name
) {}

