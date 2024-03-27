package com.example.webapp.Interceptors;

import com.example.webapp.DAO.UserDAO;
import com.example.webapp.controller.UserController;
import com.example.webapp.controlleradvice.UserDoesNotExistException;
import com.example.webapp.model.User;
import com.example.webapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
public class BasicAuthInterceptor implements HandlerInterceptor {

    @Autowired
    UserDAO userDAO;

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Logger logger = LogManager.getLogger(BasicAuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws UserDoesNotExistException, IOException {

        if (handler instanceof HandlerMethod) {
            if(request.getMethod().equalsIgnoreCase("OPTIONS") || request.getMethod().equalsIgnoreCase("HEAD")){
                response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
                UUID correlationId = UUID.randomUUID();
                log(request,response,correlationId);
                return false;
            }if(request.getMethod().equalsIgnoreCase("Get") &&request.getRequestURI().equalsIgnoreCase("/v1/user/authenticate") && request.getParameterMap().size()>0){
                return true;
            }if(request.getParameterMap().size()>0){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                UUID correlationId = UUID.randomUUID();
                log(request,response,correlationId);
                return false;
            }
            if (request.getMethod().equalsIgnoreCase("GET") || request.getMethod().equalsIgnoreCase("PUT")) {
                // Extract Base64 encoded credentials from the Authorization header
                String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
                    String base64Credentials = authorizationHeader.substring("Basic ".length());
                    String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);

                    // Split the decoded credentials into username and password
                    String[] usernamePassword = credentials.split(":", 2);
                    if (usernamePassword.length == 2) {
                        String username = usernamePassword[0];
                        String password = usernamePassword[1];

                        User user = userService.getUser(username);
                        if(!user.isAuthenticated()){
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"" +"User not verified"+ "\"}");
                            UUID correlationId = UUID.randomUUID();
                            log(request,response,correlationId);
                            return false;
                        }

                        // Check if the user exists and the password matches
                        if (!passwordEncoder.matches(password, user.getPassword())) {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"" +"Password Mismatch"+ "\"}");
                            UUID correlationId = UUID.randomUUID();
                            log(request,response,correlationId);

                            return false;
                        }
                        request.setAttribute("user", user);
                    }
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\": \"" +"Authorization Details not provided"+ "\"}");
                    UUID correlationId = UUID.randomUUID();
                    log(request,response,correlationId);
                    return false;
                }
            }
        }
        return true;
    }

    private void log(HttpServletRequest request, HttpServletResponse response, UUID correlationId) {;
        HttpStatus httpStatus = HttpStatus.resolve(response.getStatus());
        if (httpStatus != null) {
            if ((httpStatus.value() >= HttpStatus.BAD_REQUEST.value() && httpStatus.value() <= HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value()) || (httpStatus.value() >= HttpStatus.INTERNAL_SERVER_ERROR.value() && httpStatus.value() <= HttpStatus.NETWORK_AUTHENTICATION_REQUIRED.value())) {
                logger.error("{'traceID': {},'method': {} , 'uri': {},'statusCode': {}, 'errorMessage': {}}",
                        correlationId, request.getMethod(), request.getRequestURI(), response.getStatus(), response.toString());
            } else {
                logger.info("{'traceID': {},'method': {} , 'uri': {},'statusCode': {},'responseBody': {}}",
                        correlationId, request.getMethod(), request.getRequestURI(), response.getStatus(),response.toString());
            }
        } else {
            logger.error("{'traceID': {},'method': {} , 'uri': {}, 'errorMessage': {}}",
                    correlationId, request.getMethod(), request.getRequestURI(), response.toString());
        }

    }

}
