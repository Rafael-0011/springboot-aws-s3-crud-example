package com.example.testS3.mapper;

import com.example.testS3.domain.Document;
import com.example.testS3.dto.DocumentResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper para converter Document (entidade) para DocumentResponse (DTO).
 * Separação de responsabilidades: conversão centralizada e reutilizável.
 */
@Component
public class DocumentMapper {

    /**
     * Converte uma entidade Document para seu DTO de resposta.
     *
     * @param document a entidade a ser convertida
     * @return DocumentResponse com os dados do documento
     */
    public DocumentResponse toResponse(Document document) {
        if (document == null) {
            return null;
        }

        return new DocumentResponse(
            document.getId(),
            document.getName(),
            document.getContentType(),
            document.getFileSize(),
            document.getCreatedAt(),
            document.getUpdatedAt()
        );
    }

    /**
     * Converte uma entidade Document para seu DTO de resposta de forma nula-segura.
     *
     * @param document a entidade a ser convertida
     * @return DocumentResponse ou null se document for null
     */
    public DocumentResponse toResponseNullSafe(Document document) {
        return document != null ? toResponse(document) : null;
    }
}

