package com.example.testS3.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Configuração do cliente AWS S3.
 *
 * Segurança:
 * - Priorizando credenciais da variável de ambiente/IAM Role
 * - Fallback para credenciais estáticas apenas se explicitamente configurado
 * - Nunca commita credenciais no código
 */
@Slf4j
@Configuration
public class AwsS3Config {

    @Value("${aws.s3.access-key:}")
    private String awsAccessKey;

    @Value("${aws.s3.secret-key:}")
    private String awsSecretKey;

    @Value("${spring.cloud.aws.region.static:sa-east-1}")
    private String awsRegion;

    /**
     * Cria um cliente S3 configurado.
     * Utiliza IAM Role (recomendado) ou credenciais estáticas como fallback.
     *
     * @return S3Client configurado
     */
    @Bean
    public S3Client s3Client() {
        log.info("Inicializando cliente S3 para região: {}", awsRegion);

        S3Client s3Client;

        // Tenta usar credenciais do ambiente/IAM Role (RECOMENDADO)
        if (awsAccessKey.isEmpty() && awsSecretKey.isEmpty()) {
            log.info("Usando credenciais padrão do ambiente (IAM Role ou variáveis de ambiente)");
            s3Client = S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        } else {
            // Fallback para credenciais estáticas (apenas para desenvolvimento local)
            log.warn("Utilizando credenciais estáticas. Em produção, use IAM Roles!");
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                awsAccessKey,
                awsSecretKey
            );

            s3Client = S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
        }

        log.info("Cliente S3 inicializado com sucesso");
        return s3Client;
    }
}

