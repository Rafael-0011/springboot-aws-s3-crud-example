package com.example.testS3.service;

import com.example.testS3.dto.CreateDocumentRequest;
import com.example.testS3.dto.DocumentResponse;
import com.example.testS3.dto.UpdateDocumentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Interface que define os contratos para operações de negócio relacionadas a documentos.
 * Coordena entre o acesso a dados (repository) e a integração com o AWS S3.
 * Implementações desta interface encapsulam a lógica de negócio da aplicação.
 */
public interface IDocumentService {

    /**
     * Cria um novo documento realizando upload do arquivo para o AWS S3.
     * O arquivo é enviado para o S3 e os metadados são persistidos no banco de dados.
     *
     * @param file arquivo MultipartFile contendo os dados a serem enviados
     * @param request CreateDocumentRequest com informações do documento (nome, tipo de conteúdo)
     * @return DocumentResponse contendo os dados do documento criado
     * @throws com.example.testS3.exception.ValidationException se o arquivo for inválido
     * @throws com.example.testS3.exception.S3OperationException se o upload para S3 falhar
     */
    DocumentResponse createDocument(MultipartFile file, CreateDocumentRequest request);

    /**
     * Busca um documento específico pelo seu ID UUID.
     * Recupera o documento do banco de dados.
     *
     * @param id identificador único (UUID) do documento
     * @return DocumentResponse contendo os dados do documento
     * @throws com.example.testS3.exception.ResourceNotFoundException se o documento não existir
     */
    DocumentResponse getDocumentById(UUID id);

    /**
     * Lista todos os documentos com suporte a paginação.
     * Permite navegar através de grandes conjuntos de dados de forma eficiente.
     *
     * @param pageable objeto Pageable contendo informações de paginação
     * @return Page de DocumentResponse contendo documentos paginados
     */
    Page<DocumentResponse> getAllDocuments(Pageable pageable);

    /**
     * Lista todos os documentos sem paginação.
     * Recupera a lista completa de documentos do banco de dados.
     * Use com cuidado em bases de dados grandes.
     *
     * @return List de DocumentResponse contendo todos os documentos
     */
    List<DocumentResponse> getAllDocuments();

    /**
     * Atualiza os dados de um documento existente.
     * Atualmente permite atualizar apenas o nome do documento.
     * Os metadados do arquivo (tamanho, tipo) não são alterados.
     *
     * @param id identificador único (UUID) do documento a atualizar
     * @param request UpdateDocumentRequest contendo novos dados
     * @return DocumentResponse contendo os dados atualizados
     * @throws com.example.testS3.exception.ResourceNotFoundException se o documento não existir
     */
    DocumentResponse updateDocument(UUID id, UpdateDocumentRequest request);

    /**
     * Atualiza o arquivo associado a um documento existente.
     * Remove o arquivo antigo do S3 e faz upload do novo arquivo.
     * Os metadados (tamanho, tipo) são atualizados com base no novo arquivo.
     *
     * @param id identificador único (UUID) do documento
     * @param file novo arquivo MultipartFile
     * @return DocumentResponse contendo os dados atualizados
     * @throws com.example.testS3.exception.ResourceNotFoundException se o documento não existir
     * @throws com.example.testS3.exception.ValidationException se o novo arquivo for inválido
     * @throws com.example.testS3.exception.S3OperationException se operações S3 falharem
     */
    DocumentResponse updateDocumentFile(UUID id, MultipartFile file);

    /**
     * Deleta um documento e seu arquivo associado no S3.
     * Remove os metadados do banco de dados e o arquivo do S3.
     * Esta operação é permanente e não pode ser revertida.
     *
     * @param id identificador único (UUID) do documento a deletar
     * @throws com.example.testS3.exception.ResourceNotFoundException se o documento não existir
     * @throws com.example.testS3.exception.S3OperationException se a exclusão do S3 falhar
     */
    void deleteDocument(UUID id);

    /**
     * Obtém o arquivo de um documento do AWS S3.
     * Realiza download do arquivo usando a chave S3 armazenada nos metadados.
     *
     * @param id identificador único (UUID) do documento
     * @return array de bytes contendo o conteúdo do arquivo
     * @throws com.example.testS3.exception.ResourceNotFoundException se o documento não existir
     * @throws com.example.testS3.exception.S3OperationException se o download falhar
     */
    byte[] downloadDocumentFile(UUID id);
}

