package com.product.filthilink.exceptions;

import com.product.filthilink.entities.dtos.ApiErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDto> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach((FieldError fieldError) -> fieldErrors.put(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ));

        return build("Validation failed", HttpStatus.BAD_REQUEST, request, fieldErrors);
    }

    @ExceptionHandler(Unauthorized.class)
    public ResponseEntity<ApiErrorResponseDto> handleUnauthorized( Unauthorized ex, HttpServletRequest request ) {
        return build(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ApiErrorResponseDto> handleNotFound(ResourceNotFound ex, HttpServletRequest request) {

        return build(ex.getMessage(), HttpStatus.NOT_FOUND, request);

    }

    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<ApiErrorResponseDto> handleBadRequest(BadRequest ex, HttpServletRequest request) {
        return build(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResourceConflict.class)
    public ResponseEntity<ApiErrorResponseDto> handleConflict(ResourceConflict ex, HttpServletRequest request) {
        return build(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(BadCredentials.class)
    public ResponseEntity<ApiErrorResponseDto> handleBadCredentials(BadCredentials ex, HttpServletRequest request) {

        log.warn("Bad credentials attempt at {}", request.getRequestURI());


        return build("Invalid email or password", HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(DodoPaymentsException.class)
    public ResponseEntity<ApiErrorResponseDto> handleDodoPayments(DodoPaymentsException ex, HttpServletRequest request) {

        log.error("Dodo payments api error at {}: {} - cause{}", request.getRequestURI(),
                ex.getMessage(),
                ex.getCause() != null ? ex.getCause().getMessage() : "none"
                );


        return build("Payment processing error. Please try again.", HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponseDto> handleResponseStatus(
            ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            log.warn("Unresolvable HTTP status code {} at {}", ex.getStatusCode().value(), request.getRequestURI());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return build(ex.getReason(), status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ApiErrorResponseDto> build(String message, HttpStatus status, HttpServletRequest request) {


        return build(message, status, request, null);
    }

    private ResponseEntity<ApiErrorResponseDto> build(String message, HttpStatus status, HttpServletRequest request, Map<String, String > fieldErrors) {

        ApiErrorResponseDto body = new ApiErrorResponseDto(
                message,
                request.getRequestURI(),
                status.value(),
                fieldErrors,
                LocalDateTime.now()

        );

        return ResponseEntity.status(status).body(body);
    }
}
