# Spring Boot AWS S3 CRUD Example

## Sobre o projeto
Este projeto é uma API REST em **Spring Boot** para gerenciamento de documentos.
Ele permite criar, listar, atualizar, excluir e baixar documentos, usando:

- **PostgreSQL** para metadados
- **AWS S3** para armazenamento dos arquivos
- **Flyway** para versionamento de banco
- **Swagger/OpenAPI** para documentação da API

## O que o projeto faz
- Upload de arquivo com cadastro de documento
- Consulta de documento por ID
- Listagem de documentos (com e sem paginação)
- Atualização dos dados do documento
- Atualização do arquivo associado ao documento
- Download de arquivo
- Remoção de documento e arquivo

## Pré-requisitos
- **Java 21**
- **PostgreSQL** em execução
- **Conta AWS** com acesso ao S3
- **Bucket S3** criado

## Configuração necessária
As principais configurações estão em `src/main/resources/application.properties`.
Você pode usar variáveis de ambiente:

- `DB_URL` (ex.: `jdbc:postgresql://localhost:5432/tests3`)
- `DB_USER` (ex.: `postgres`)
- `DB_PASSWORD`
- `AWS_S3_BUCKET_NAME`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

> A região padrão está como `sa-east-1`.

## Como rodar o projeto
1. Configure as variáveis de ambiente acima.
2. Garanta que o PostgreSQL esteja acessível.
3. Execute o projeto:

```bash
./mvnw spring-boot:run
```

## Como testar
Para rodar os testes automatizados:

```bash
./mvnw test
```

## Documentação da API
Com a aplicação em execução:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
