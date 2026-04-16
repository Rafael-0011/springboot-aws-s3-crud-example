package com.example.testS3.repository;

import com.example.testS3.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para acesso aos dados de Document na camada de persistência.
 * Estende JpaRepository para operações CRUD padrão.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    /**
     * Busca um documento pelo nome do arquivo no S3.
     *
     * @param s3Key a chave do arquivo no S3
     * @return Optional contendo o documento se encontrado
     */
    Optional<Document> findByS3Key(String s3Key);

    /**
     * Verifica se um documento com a chave S3 existe.
     *
     * @param s3Key a chave do arquivo no S3
     * @return true se existe, false caso contrário
     */
    boolean existsByS3Key(String s3Key);
}

