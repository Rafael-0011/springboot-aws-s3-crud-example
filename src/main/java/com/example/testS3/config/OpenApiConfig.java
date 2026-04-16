package com.example.testS3.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI/Swagger para documentação automática da API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .addServersItem(new Server()
                .url("http://localhost:8080")
                .description("Servidor Local"))
            .info(new Info()
                .title("TestS3 API - CRUD com AWS S3")
                .version("1.0.0")
                .description("API RESTful para gerenciamento de documentos com armazenamento em AWS S3")
                .contact(new Contact()
                    .name("Suporte")
                    .email("support@example.com")));
    }
}

