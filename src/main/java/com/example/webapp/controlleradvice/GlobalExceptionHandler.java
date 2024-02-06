package com.example.webapp.controlleradvice;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity MethodNotSupportedExceptionhandler(HttpServletResponse response) {
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma","no-cache");
        response.setHeader("X-Content-Type-Options","nosniff");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @ExceptionHandler({UserExistsException.class,InvalidUserUpdaRequestException.class,IllegalArgumentException.class})
    public ResponseEntity BadRequestHandler(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity UserDoesNotExistExceptionHandler(Exception exception) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((error1, error2) -> error1 + "\n" + error2)
                .orElse("Invalid input");

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
