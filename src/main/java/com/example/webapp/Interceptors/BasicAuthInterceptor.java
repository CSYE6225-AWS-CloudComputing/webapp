package com.example.webapp.Interceptors;

import com.example.webapp.DAO.UserDAO;
import com.example.webapp.controlleradvice.UserDoesNotExistException;
import com.example.webapp.model.User;
import com.example.webapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class BasicAuthInterceptor implements HandlerInterceptor {

    @Autowired
    UserDAO userDAO;

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws UserDoesNotExistException, IOException {

        if (handler instanceof HandlerMethod) {
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

                        // Check if the user exists and the password matches
                        if (!passwordEncoder.matches(password, user.getPassword())) {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"" +"Password Mismatch"+ "\"}");

                            return false;
                        }
                        request.setAttribute("user", user);
                    }
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\": \"" +"Authorization Details not provided"+ "\"}");
                    return false;
                }
            }
        }
        return true;
    }

}
