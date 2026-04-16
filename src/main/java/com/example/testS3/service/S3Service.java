package com.example.testS3.service;

import com.example.testS3.exception.S3OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.UUID;

/**
 * Serviço para operações com AWS S3.
 * Encapsula a lógica de upload, download e exclusão de arquivos.
 */
@Slf4j
@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Realiza upload de um arquivo para o S3.
     *
     * @param fileData dados do arquivo
     * @param contentType tipo de conteúdo do arquivo
     * @param originalFileName nome original do arquivo
     * @return a chave S3 do arquivo enviado
     * @throws S3OperationException se o upload falhar
     */
    public String uploadFile(byte[] fileData, String contentType, String originalFileName) {
        if (fileData == null || fileData.length == 0) {
            throw new S3OperationException("Arquivo vazio não pode ser enviado");
        }

        // Gera uma chave S3 única para evitar conflitos
        String s3Key = generateS3Key(originalFileName);

        try {
            log.debug("Iniciando upload do arquivo: {} para S3 com chave: {}", originalFileName, s3Key);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .metadata(java.util.Map.of(
                    "original-filename", originalFileName,
                    "upload-timestamp", String.valueOf(System.currentTimeMillis())
                ))
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));

            log.info("Arquivo uploadado com sucesso: {} -> chave S3: {}", originalFileName, s3Key);
            return s3Key;

        } catch (S3Exception e) {
            log.error("Erro ao fazer upload para S3: {}", e.getMessage(), e);
            throw new S3OperationException("Falha ao fazer upload do arquivo: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao fazer upload: {}", e.getMessage(), e);
            throw new S3OperationException("Erro inesperado ao fazer upload: " + e.getMessage(), e);
        }
    }

    /**
     * Realiza download de um arquivo do S3.
     *
     * @param s3Key chave do arquivo no S3
     * @return dados do arquivo
     * @throws S3OperationException se o download falhar
     */
    public byte[] downloadFile(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            throw new S3OperationException("Chave S3 inválida");
        }

        try {
            log.debug("Iniciando download do arquivo: {}", s3Key);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

            byte[] content = s3Client.getObject(getObjectRequest).readAllBytes();

            log.info("Arquivo baixado com sucesso: {}", s3Key);
            return content;

        } catch (NoSuchKeyException e) {
            log.warn("Arquivo não encontrado no S3: {}", s3Key);
            throw new S3OperationException("Arquivo não encontrado: " + s3Key, e);
        } catch (S3Exception e) {
            log.error("Erro ao baixar arquivo do S3: {}", e.getMessage(), e);
            throw new S3OperationException("Falha ao baixar arquivo: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao baixar arquivo: {}", e.getMessage(), e);
            throw new S3OperationException("Erro inesperado ao baixar arquivo: " + e.getMessage(), e);
        }
    }

    /**
     * Deleta um arquivo do S3.
     *
     * @param s3Key chave do arquivo no S3
     * @throws S3OperationException se a exclusão falhar
     */
    public void deleteFile(String s3Key) {
        if (s3Key == null || s3Key.isBlank()) {
            throw new S3OperationException("Chave S3 inválida");
        }

        try {
            log.debug("Iniciando exclusão do arquivo: {}", s3Key);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

            s3Client.deleteObject(deleteObjectRequest);

            log.info("Arquivo excluído com sucesso: {}", s3Key);

        } catch (S3Exception e) {
            log.error("Erro ao deletar arquivo do S3: {}", e.getMessage(), e);
            throw new S3OperationException("Falha ao deletar arquivo: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao deletar arquivo: {}", e.getMessage(), e);
            throw new S3OperationException("Erro inesperado ao deletar arquivo: " + e.getMessage(), e);
        }
    }

    /**
     * Gera uma chave S3 única para um arquivo.
     * Usa UUID para garantir unicidade e evitar conflitos.
     *
     * @param originalFileName nome original do arquivo
     * @return chave S3 formatada
     */
    private String generateS3Key(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        String extension = getFileExtension(originalFileName);
        return "documents/" + uuid + extension;
    }

    /**
     * Extrai a extensão do arquivo mantendo o ponto.
     *
     * @param fileName nome do arquivo
     * @return extensão com ponto (ex: ".pdf") ou string vazia
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }
}

