package com.example.testS3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO para documentação do Swagger para upload de documento com multipart/form-data.
 * Esta classe é usada apenas para gerar a documentação correta no Swagger UI.
 * Não é utilizada em tempo de execução.
 */
@Schema(name = "CreateDocumentMultipartRequest", description = "Dados para criar um novo documento com upload de arquivo")
public class CreateDocumentMultipartRequest {

    @Schema(description = "Arquivo a ser enviado", type = "string", format = "binary")
    private byte[] file;

    @Schema(description = "Nome do documento", example = "Meu Documento", minLength = 3, maxLength = 255)
    private String name;

    @Schema(description = "Tipo MIME do arquivo", example = "application/pdf")
    private String contentType;

    // Construtores, getters e setters (não utilizados, apenas para documentação)
    public CreateDocumentMultipartRequest() {
    }

    public CreateDocumentMultipartRequest(byte[] file, String name, String contentType) {
        this.file = file;
        this.name = name;
        this.contentType = contentType;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}

