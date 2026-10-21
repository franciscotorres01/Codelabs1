package com.denkitronik.clienteservice2.cliente.advice;

import com.denkitronik.clienteservice2.cliente.exceptions.ApiErrorResponse;
import com.denkitronik.clienteservice2.cliente.exceptions.ClienteExistsException;
import com.denkitronik.clienteservice2.cliente.exceptions.ClienteNotFoundException;
import com.denkitronik.clienteservice2.cliente.exceptions.ClienteServiceException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Error 404 - Cliente no encontrado
    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ClienteNotFoundException ex, HttpServletRequest request) {
        logger.warn("Cliente no encontrado: {}", ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 2. Error 409 - Email duplicado
    @ExceptionHandler(ClienteExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleExists(ClienteExistsException ex, HttpServletRequest request) {
        logger.warn("Conflicto de email: {}", ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 3. Error 400 - Fallos de validación (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensajes = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        logger.warn("Error de validación: {}", mensajes);
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                mensajes,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 4. Error 500 - Errores internos del servicio personalizados
    @ExceptionHandler(ClienteServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleService(ClienteServiceException ex, HttpServletRequest request) {
        logger.error("Error interno del servicio: {}", ex.getMessage(), ex);
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // 5. NUEVO: Error 500 - Errores de base de datos (Llaves foráneas, columnas nulas, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDatabaseError(DataIntegrityViolationException ex, HttpServletRequest request) {
        logger.error("Error de base de datos: {}", ex.getMostSpecificCause().getMessage());
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Error en la base de datos: " + ex.getMostSpecificCause().getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // 6. Red de Seguridad - Captura CUALQUIER otro error (Para descubrir qué está fallando)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Error inesperado en el servidor: {}", ex.getMessage(), ex);
        ex.printStackTrace(); // Imprime el error real en la consola de la plataforma

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Error inesperado: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}