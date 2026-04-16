package com.example.testS3.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para criar um novo documento.
 * Utiliza record class para imutabilidade e concisão.
 */
public record CreateDocumentRequest(
    @NotBlank(message = "Nome do documento é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    String name,

    @NotNull(message = "Arquivo é obrigatório")
    byte[] fileContent,

    @NotBlank(message = "Tipo de conteúdo é obrigatório")
    String contentType
) {}

