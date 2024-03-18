package com.example.webapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class HealthCheckController {

    private final Logger logger = LogManager.getLogger(HealthCheckController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/healthz")
    public ResponseEntity getHealth(HttpServletRequest request,HttpServletResponse response){
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma","no-cache");
        response.setHeader("X-Content-Type-Options","nosniff");
        try {

            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            UUID correlationId = UUID.randomUUID();
            long startTime = System.currentTimeMillis();
            long duration = System.currentTimeMillis() - startTime;
            log(request, new ResponseEntity<>(HttpStatus.OK), correlationId, duration);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    private void log(HttpServletRequest request, ResponseEntity<?> response, UUID correlationId, long duration) {
        logger.info("Logging From Controller");
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
