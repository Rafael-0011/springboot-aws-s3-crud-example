package com.example.testS3.service;

import com.example.testS3.domain.Document;
import com.example.testS3.dto.CreateDocumentRequest;
import com.example.testS3.dto.DocumentResponse;
import com.example.testS3.dto.UpdateDocumentRequest;
import com.example.testS3.exception.ResourceNotFoundException;
import com.example.testS3.exception.ValidationException;
import com.example.testS3.mapper.DocumentMapper;
import com.example.testS3.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Implementação da interface IDocumentService.
 * Serviço de domínio para operações de Document.
 * Encapsula a lógica de negócio e coordena entre S3Service e DocumentRepository.
 * Segue princípios SOLID, especialmente SRP e DIP.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DocumentService implements IDocumentService {

    private final DocumentRepository documentRepository;
    private final S3Service s3Service;
    private final DocumentMapper documentMapper;


    /**
     * Cria um novo documento realizando upload do arquivo para S3.
     *
     * @param file arquivo a ser enviado
     * @param request dados do documento
     * @return DocumentResponse com os dados do documento criado
     * @throws ValidationException se o arquivo for inválido
     */
    @Override
    public DocumentResponse createDocument(MultipartFile file, CreateDocumentRequest request) {
        validateFile(file);

        try {
            log.info("Criando novo documento: {}", request.name());

            // Upload do arquivo para S3
            String s3Key = s3Service.uploadFile(
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename()
            );

            // Cria a entidade Document
            Document document = Document.builder()
                .name(request.name())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .s3Key(s3Key)
                .build();

            // Persiste no banco de dados
            Document savedDocument = documentRepository.save(document);

            log.info("Documento criado com sucesso. ID: {}, S3Key: {}", savedDocument.getId(), s3Key);

            return documentMapper.toResponse(savedDocument);

        } catch (Exception e) {
            log.error("Erro ao criar documento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao processar arquivo: " + e.getMessage(), e);
        }
    }

    /**
     * Busca um documento pelo ID.
     *
     * @param id ID do documento
     * @return DocumentResponse com os dados do documento
     * @throws ResourceNotFoundException se o documento não existir
     */
    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(UUID id) {
        log.debug("Buscando documento com ID: {}", id);

        Document document = documentRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Documento não encontrado. ID: {}", id);
                return new ResourceNotFoundException("Documento não encontrado com ID: " + id);
            });

        return documentMapper.toResponse(document);
    }

    /**
     * Lista todos os documentos com paginação.
     *
     * @param pageable informações de paginação
     * @return Page de DocumentResponse
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> getAllDocuments(Pageable pageable) {
        log.debug("Listando documentos com paginação: {}", pageable);
        return documentRepository.findAll(pageable)
            .map(documentMapper::toResponse);
    }

    /**
     * Lista todos os documentos sem paginação.
     *
     * @return List de DocumentResponse
     */
    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getAllDocuments() {
        log.debug("Listando todos os documentos");
        return documentRepository.findAll().stream()
            .map(documentMapper::toResponse)
            .toList();
    }

    /**
     * Atualiza um documento existente.
     *
     * @param id ID do documento
     * @param request dados atualizados
     * @return DocumentResponse com os dados do documento atualizado
     * @throws ResourceNotFoundException se o documento não existir
     */
    @Override
    public DocumentResponse updateDocument(UUID id, UpdateDocumentRequest request) {
        log.info("Atualizando documento com ID: {}", id);

        Document document = documentRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Documento não encontrado. ID: {}", id);
                return new ResourceNotFoundException("Documento não encontrado com ID: " + id);
            });

        document.setName(request.name());

        Document updatedDocument = documentRepository.save(document);

        log.info("Documento atualizado com sucesso. ID: {}", id);

        return documentMapper.toResponse(updatedDocument);
    }

    /**
     * Atualiza o arquivo de um documento existente.
     *
     * @param id ID do documento
     * @param file novo arquivo
     * @return DocumentResponse com os dados do documento atualizado
     * @throws ResourceNotFoundException se o documento não existir
     * @throws ValidationException se o arquivo for inválido
     */
    @Override
    public DocumentResponse updateDocumentFile(UUID id, MultipartFile file) {
        validateFile(file);

        log.info("Atualizando arquivo do documento com ID: {}", id);

        Document document = documentRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Documento não encontrado. ID: {}", id);
                return new ResourceNotFoundException("Documento não encontrado com ID: " + id);
            });

        try {
            // Delete do arquivo antigo
            String oldS3Key = document.getS3Key();
            s3Service.deleteFile(oldS3Key);
            log.debug("Arquivo antigo deletado: {}", oldS3Key);

            // Upload do novo arquivo
            String newS3Key = s3Service.uploadFile(
                file.getBytes(),
                file.getContentType(),
                file.getOriginalFilename()
            );

            // Atualização dos dados
            document.setContentType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setS3Key(newS3Key);

            Document updatedDocument = documentRepository.save(document);

            log.info("Arquivo do documento atualizado com sucesso. ID: {}", id);

            return documentMapper.toResponse(updatedDocument);

        } catch (Exception e) {
            log.error("Erro ao atualizar arquivo do documento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao processar arquivo: " + e.getMessage(), e);
        }
    }

    /**
     * Deleta um documento (deleta do S3 e do banco de dados).
     *
     * @param id ID do documento
     * @throws ResourceNotFoundException se o documento não existir
     */
    @Override
    public void deleteDocument(UUID id) {
        log.info("Deletando documento com ID: {}", id);

        Document document = documentRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Documento não encontrado. ID: {}", id);
                return new ResourceNotFoundException("Documento não encontrado com ID: " + id);
            });

        try {
            // Delete do arquivo S3
            s3Service.deleteFile(document.getS3Key());

            // Delete do banco de dados
            documentRepository.deleteById(id);

            log.info("Documento deletado com sucesso. ID: {}", id);

        } catch (Exception e) {
            log.error("Erro ao deletar documento: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao deletar documento: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém o arquivo de um documento do S3.
     *
     * @param id ID do documento
     * @return bytes do arquivo
     * @throws ResourceNotFoundException se o documento não existir
     */
    @Override
    @Transactional(readOnly = true)
    public byte[] downloadDocumentFile(UUID id) {
        log.debug("Fazendo download do arquivo do documento com ID: {}", id);

        Document document = documentRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Documento não encontrado. ID: {}", id);
                return new ResourceNotFoundException("Documento não encontrado com ID: " + id);
            });

        return s3Service.downloadFile(document.getS3Key());
    }

    /**
     * Valida um arquivo MultipartFile.
     *
     * @param file arquivo a validar
     * @throws ValidationException se o arquivo for inválido
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("Arquivo é obrigatório");
        }

        if (file.getSize() > 100 * 1024 * 1024) { // 100MB
            throw new ValidationException("Arquivo não pode exceder 100MB");
        }

        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            throw new ValidationException("Nome do arquivo é inválido");
        }
    }
}

