package com.example.webapp.controlleradvice;

import jakarta.servlet.http.HttpServletRequest;
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
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity MethodNotSupportedExceptionHandler(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma","no-cache");
        response.setHeader("X-Content-Type-Options","nosniff");
        createLog(request,ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @ExceptionHandler({UserExistsException.class,InvalidUserUpdaRequestException.class,IllegalArgumentException.class,InvalidCreateRequest.class})
    public ResponseEntity<Object> BadRequestHandler(HttpServletRequest request,Exception exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("msg", exception.getMessage());
        responseMap.put("timestamp", LocalDateTime.now());
        createLog(request,new ResponseEntity<>(responseMap, status));
        return new ResponseEntity<>(responseMap, status);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> HttpMessageNotReadableExceptionHandler(HttpServletRequest request,Exception exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("msg", "Bad JSON: Couldn't Parse");
        responseMap.put("timestamp", LocalDateTime.now());
        createLog(request,new ResponseEntity<>(responseMap, status));
        return new ResponseEntity<>(responseMap, status);
    }

    @ExceptionHandler(PubSubException.class)
    public ResponseEntity<Object> PubSubExceptionHandler(HttpServletRequest request,Exception exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("msg", "PubSub Credentials path invalid");
        responseMap.put("timestamp", LocalDateTime.now());
        createLog(request,new ResponseEntity<>(responseMap, status));
        return new ResponseEntity<>(responseMap, status);
    }

    @ExceptionHandler({UserDoesNotExistException.class, IOException.class})
    public ResponseEntity<Object> UserDoesNotExistExceptionHandler(HttpServletRequest request,Exception exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("msg", exception.getMessage());
        responseMap.put("timestamp", LocalDateTime.now());
        createLog(request,new ResponseEntity<>(responseMap, status));
        return new ResponseEntity<>(responseMap, status);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity NoResourceFoundExceptionHandler(HttpServletRequest request,HttpServletResponse response) {
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma","no-cache");
        response.setHeader("X-Content-Type-Options","nosniff");
        createLog(request,ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(HttpServletRequest request,MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Invalid input");

        Map<String, List<String>> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));

        response.put("validationErrors", validationErrors);
        createLog(request,new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    private void createLog(HttpServletRequest request,ResponseEntity<?> response){
        UUID correlationId = UUID.randomUUID();
        long startTime = System.currentTimeMillis();
        long duration = System.currentTimeMillis() - startTime;
        log(request, response, correlationId, duration);
    }

    private void log(HttpServletRequest request, ResponseEntity<?> response, UUID correlationId, long duration) {
        HttpStatus httpStatus = HttpStatus.resolve(response.getStatusCode().value());
        if (httpStatus != null) {
            logger.error("{'traceID': {},'method': {} , 'uri': {},'statusCode': {}, 'errorMessage': {},'duration': {}}",
                    correlationId, request.getMethod(), request.getRequestURI(), response.getStatusCode().value(), response.toString(), duration);
        } else {
            logger.error("{'traceID': {},'method': {} , 'uri': {}, 'errorMessage': {}, 'duration': {}}",
                    correlationId, request.getMethod(), request.getRequestURI(), response.toString(), duration);
        }
    }

}
