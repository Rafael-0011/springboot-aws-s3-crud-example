package com.example.testS3.controller;

import com.example.testS3.dto.CreateDocumentMultipartRequest;
import com.example.testS3.dto.DocumentResponse;
import com.example.testS3.dto.UpdateDocumentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Interface que define os contratos para operações CRUD de documentos.
 * Contém todas as documentações OpenAPI/Swagger dos endpoints.
 * As implementações desta interface lidam com requisições HTTP.
 */
public interface IDocumentController {

    /**
     * Cria um novo documento com upload de arquivo para o S3.
     *
     * @param file arquivo a ser enviado (MultipartFile)
     * @param name nome do documento (3-255 caracteres)
     * @param contentType tipo MIME do arquivo (ex: application/pdf)
     * @return ResponseEntity contendo DocumentResponse com dados do documento criado
     */
    @Operation(
        summary = "Criar novo documento",
        description = "Cria um novo documento e faz upload do arquivo para S3",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "multipart/form-data",
                schema = @Schema(implementation = CreateDocumentMultipartRequest.class)
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Documento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou arquivo inválido"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<DocumentResponse> createDocument(
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "Nome do documento") String name,
            @Parameter(description = "Tipo MIME do arquivo") String contentType);

    /**
     * Busca um documento específico pelo seu ID UUID.
     *
     * @param id identificador único (UUID) do documento
     * @return ResponseEntity contendo DocumentResponse com os dados do documento
     */
    @Operation(summary = "Buscar documento por ID", description = "Retorna os dados de um documento específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documento encontrado"),
        @ApiResponse(responseCode = "404", description = "Documento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<DocumentResponse> getDocument(
            @Parameter(description = "ID do documento") UUID id);

    /**
     * Lista todos os documentos com suporte a paginação.
     * Permite controle sobre tamanho da página, número da página e ordenação.
     *
     * @param pageable objeto contendo informações de paginação (page, size, sort)
     * @return ResponseEntity contendo Page de DocumentResponse
     */
    @Operation(summary = "Listar documentos com paginação", description = "Retorna lista paginada de documentos")
    @ApiResponse(responseCode = "200", description = "Documentos listados com sucesso")
    ResponseEntity<Page<DocumentResponse>> getAllDocuments(Pageable pageable);

    /**
     * Lista todos os documentos sem paginação.
     * Retorna a lista completa de documentos em uma única resposta.
     *
     * @return ResponseEntity contendo List de DocumentResponse
     */
    @Operation(summary = "Listar todos os documentos", description = "Retorna lista completa de documentos sem paginação")
    @ApiResponse(responseCode = "200", description = "Documentos listados com sucesso")
    ResponseEntity<List<DocumentResponse>> getAllDocumentsNoPagination();

    /**
     * Atualiza os dados de um documento existente.
     * Atualmente permite atualizar apenas o nome do documento.
     *
     * @param id identificador único (UUID) do documento a atualizar
     * @param request UpdateDocumentRequest contendo dados atualizados
     * @return ResponseEntity contendo DocumentResponse com dados atualizados
     */
    @Operation(summary = "Atualizar documento", description = "Atualiza nome de um documento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documento atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Documento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<DocumentResponse> updateDocument(
            @Parameter(description = "ID do documento") UUID id,
            UpdateDocumentRequest request);

    /**
     * Atualiza o arquivo associado a um documento existente.
     * Remove o arquivo antigo do S3 e faz upload do novo arquivo.
     *
     * @param id identificador único (UUID) do documento
     * @param file novo arquivo para substituir o anterior
     * @return ResponseEntity contendo DocumentResponse com dados atualizados
     */
    @Operation(
        summary = "Atualizar arquivo do documento",
        description = "Substitui o arquivo de um documento no S3",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = "multipart/form-data")
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Arquivo atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Arquivo inválido"),
        @ApiResponse(responseCode = "404", description = "Documento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<DocumentResponse> updateDocumentFile(
            @Parameter(description = "ID do documento") UUID id,
            @RequestPart("file") MultipartFile file);

    /**
     * Deleta um documento e seu arquivo associado no S3.
     * Esta operação é permanente e não pode ser revertida.
     *
     * @param id identificador único (UUID) do documento a deletar
     * @return ResponseEntity vazio com status 204 No Content
     */
    @Operation(summary = "Deletar documento", description = "Deleta um documento e seu arquivo do S3")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Documento deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Documento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<Void> deleteDocument(
            @Parameter(description = "ID do documento") UUID id);

    /**
     * Faz download do arquivo associado a um documento.
     * Retorna os bytes do arquivo com headers apropriados para download.
     *
     * @param id identificador único (UUID) do documento
     * @return ResponseEntity contendo bytes do arquivo para download
     */
    @Operation(summary = "Download do arquivo", description = "Retorna o arquivo de um documento para download")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Arquivo retornado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Documento ou arquivo não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<byte[]> downloadDocument(
            @Parameter(description = "ID do documento") UUID id);
}

