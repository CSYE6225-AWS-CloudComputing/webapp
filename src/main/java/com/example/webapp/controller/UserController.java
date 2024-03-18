package com.example.webapp.controller;

import com.example.webapp.DTO.UserDTO;
import com.example.webapp.DTO.UserUpdateDTO;
import com.example.webapp.controlleradvice.InvalidCreateRequest;
import com.example.webapp.controlleradvice.InvalidUserUpdaRequestException;
import com.example.webapp.controlleradvice.UserDoesNotExistException;
import com.example.webapp.controlleradvice.UserExistsException;
import com.example.webapp.model.User;
import com.example.webapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/user")
@Validated
public class UserController {

    private final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @GetMapping("/self")
    public ResponseEntity<User> getUser(HttpServletRequest request) {

        User userOutput=(User) request.getAttribute("user");
        UUID correlationId = UUID.randomUUID();
        long startTime = System.currentTimeMillis();
        long duration = System.currentTimeMillis() - startTime;
        log(request, ResponseEntity.ok(userOutput), correlationId, duration);
        return ResponseEntity.ok(userOutput);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO,HttpServletRequest request) throws UserExistsException, InvalidCreateRequest {
        User userOutput=userService.createUser(userDTO);
        UUID correlationId = UUID.randomUUID();
        long startTime = System.currentTimeMillis();
        long duration = System.currentTimeMillis() - startTime;
        log(request, ResponseEntity.ok(userOutput), correlationId, duration);
       return ResponseEntity.ok(userOutput);
    }

    @PutMapping("/self")
    public ResponseEntity<User> updateUser(@Valid @RequestBody UserUpdateDTO userRequestBody, HttpServletRequest request) throws UserDoesNotExistException, InvalidUserUpdaRequestException {

        User userOutput=userService.updateUser(userRequestBody, (User) request.getAttribute("user"));
        UUID correlationId = UUID.randomUUID();
        long startTime = System.currentTimeMillis();
        long duration = System.currentTimeMillis() - startTime;
        log(request, ResponseEntity.status(204).body(userOutput), correlationId, duration);
        return ResponseEntity.status(204).body(userOutput);
    }

    private void log(HttpServletRequest request, ResponseEntity<?> response, UUID correlationId, long duration) {
        HttpStatus httpStatus = HttpStatus.resolve(response.getStatusCode().value());
        if (httpStatus != null) {
            if ((httpStatus.value() >= HttpStatus.BAD_REQUEST.value() && httpStatus.value() <= HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value()) || (httpStatus.value() >= HttpStatus.INTERNAL_SERVER_ERROR.value() && httpStatus.value() <= HttpStatus.NETWORK_AUTHENTICATION_REQUIRED.value())) {
                logger.error("{'traceID': {},'method': {} , 'uri': {},'statusCode': {}, 'errorMessage': {},'duration': {}}",
                        correlationId, request.getMethod(), request.getRequestURI(), response.getStatusCode().value(), response.toString(), duration);
            } else {
                logger.info("{'traceID': {},'method': {} , 'uri': {},'statusCode': {}, 'duration': {}}",
                        correlationId, request.getMethod(), request.getRequestURI(), response.getStatusCode().value(), duration);
            }
        } else {
            logger.error("{'traceID': {},'method': {} , 'uri': {}, 'errorMessage': {}, 'duration': {}}",
                    correlationId, request.getMethod(), request.getRequestURI(), response.toString(), duration);
        }

    }
}
