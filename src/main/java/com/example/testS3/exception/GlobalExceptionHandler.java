package com.example.testS3.exception;

import com.example.testS3.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manipulador global de exceções para toda a aplicação.
 * Centraliza o tratamento de erros e garante resposta padronizada.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Trata ResourceNotFoundException.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {

        log.warn("Recurso não encontrado: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now().format(formatter),
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Trata S3OperationException.
     */
    @ExceptionHandler(S3OperationException.class)
    public ResponseEntity<ErrorResponse> handleS3OperationException(
            S3OperationException ex,
            WebRequest request) {

        log.error("Erro na operação S3: {}", ex.getMessage(), ex);

        ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro ao processar arquivo no S3: " + ex.getMessage(),
            LocalDateTime.now().format(formatter),
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Trata ValidationException.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            WebRequest request) {

        log.warn("Erro de validação: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now().format(formatter),
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Trata erros de validação de argumento de método (Spring validation).
     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex,
//            WebRequest request) {
//
//        String message = ex.getBindingResult().getFieldErrors().stream()
//            .map(error -> error.getField() + ": " + error.getDefaultMessage())
//            .reduce((a, b) -> a + ", " + b)
//            .orElse("Erro de validação");
//
//        log.warn("Erro de validação de argumento: {}", message);
//
//        ErrorResponse response = new ErrorResponse(
//            HttpStatus.BAD_REQUEST.value(),
//            "Validação falhou: " + message,
//            LocalDateTime.now().format(formatter),
//            request.getDescription(false).replace("uri=", "")
//        );
//
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }

    /**
     * Trata exceções genéricas não previstas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        log.error("Erro inesperado: {}", ex.getMessage(), ex);

        ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno do servidor. Por favor, tente novamente mais tarde.",
            LocalDateTime.now().format(formatter),
            request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

