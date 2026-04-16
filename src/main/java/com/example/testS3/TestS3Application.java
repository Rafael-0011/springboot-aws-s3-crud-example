package com.example.testS3;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicação Spring Boot para CRUD com integração AWS S3.
 *
 * Arquitetura em camadas com:
 * - Controllers: endpoints REST
 * - Services: lógica de negócio
 * - Repositories: persistência de dados
 * - DTOs: transferência de dados
 * - Mappers: conversão entidade <-> DTO
 *
 * Segurança:
 * - Credenciais AWS via variáveis de ambiente
 * - Validação de entrada com JSR-380
 * - Tratamento global de exceções
 * - Logging estruturado
 */
@Slf4j
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "TestS3 API",
                version = "1.0.0",
                description = "API CRUD com integração AWS S3 para gerenciamento de documentos"
        )
)
public class TestS3Application {

    public static void main(String[] args) {
        SpringApplication.run(TestS3Application.class, args);
        log.info("========================================");
        log.info("TestS3 Application iniciado com sucesso!");
        log.info("Acesse a documentação em: http://localhost:8080/swagger-ui.html");
        log.info("H2 Console: http://localhost:8080/h2-console");
        log.info("========================================");
    }
}


