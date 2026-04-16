package com.example.testS3.dto;

/**
 * DTO para resposta de erro padronizada.
 */
public record ErrorResponse(
    int status,
    String message,
    String timestamp,
    String path
) {}

