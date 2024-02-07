package com.example.webapp.controlleradvice;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity MethodNotSupportedExceptionHandler(HttpServletResponse response) {
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma","no-cache");
        response.setHeader("X-Content-Type-Options","nosniff");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @ExceptionHandler({UserExistsException.class,InvalidUserUpdaRequestException.class,IllegalArgumentException.class,InvalidCreateRequest.class})
    public ResponseEntity<Object> BadRequestHandler(Exception exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("msg", exception.getMessage());
        responseMap.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(responseMap, status);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> HttpMessageNotReadableExceptionHandler(Exception exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("msg", "Bad JSON: Couldn't Parse");
        responseMap.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(responseMap, status);
    }

    @ExceptionHandler({UserDoesNotExistException.class, IOException.class})
    public ResponseEntity<Object> UserDoesNotExistExceptionHandler(Exception exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("msg", exception.getMessage());
        responseMap.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(responseMap, status);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity NoResourceFoundExceptionHandler(HttpServletResponse response) {
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma","no-cache");
        response.setHeader("X-Content-Type-Options","nosniff");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Invalid input");

        Map<String, List<String>> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));

        response.put("validationErrors", validationErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

}
