package com.jmt.franchises.exception;

import com.jmt.franchises.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        log.warn("Recurso no encontrado: {}", ex.getMessage());

        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage())));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleDuplicateResource(
            DuplicateResourceException ex) {

        log.warn("Recurso duplicado: {}", ex.getMessage());

        return Mono.just(ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage())));
    }

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleBusinessException(
            BusinessException ex) {

        log.warn("Regla de negocio violada: {}", ex.getMessage());

        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGeneralException(Exception ex) {

        log.error("Error interno no controlado: {}", ex.getMessage(), ex);

        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor. Por favor contacte al administrador.")));
    }
}
