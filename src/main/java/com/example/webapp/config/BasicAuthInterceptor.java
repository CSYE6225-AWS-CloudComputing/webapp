package com.example.webapp.config;

import com.example.webapp.DAO.UserDAO;
import com.example.webapp.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

public class BasicAuthInterceptor implements HandlerInterceptor {

    @Autowired
    UserDAO userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (request.getMethod().equalsIgnoreCase("GET") || request.getMethod().equalsIgnoreCase("PUT")){
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

                    // Retrieve user from the database by username
                    Optional<User> optionalUser = userDAO.findUserByUserNameIgnoreCase(username);
                    if(optionalUser.isEmpty()){
                        response.setStatus(HttpStatus.NO_CONTENT.value());
                        return false;
                    }
                    User user=optionalUser.get();

                    // Check if the user exists and the password matches
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        return false;
                    }
                    request.setAttribute("user",user);
                }
            }else{
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
        }

        return true;
    }

}
