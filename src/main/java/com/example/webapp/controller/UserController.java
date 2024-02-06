package com.example.webapp.controller;

import com.example.webapp.controlleradvice.InvalidUserUpdaRequestException;
import com.example.webapp.controlleradvice.UserDoesNotExistException;
import com.example.webapp.controlleradvice.UserExistsException;
import com.example.webapp.model.User;
import com.example.webapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/self")
    public ResponseEntity<User> getUser(HttpServletRequest request) throws UserDoesNotExistException {

        User userOutput=userService.getUser(request.getHeader(HttpHeaders.AUTHORIZATION));

        return ResponseEntity.ok(userOutput);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) throws UserExistsException {
        User userOutput=userService.createUser(user);
       return ResponseEntity.ok(userOutput);
    }

    @PutMapping("/self")
    public ResponseEntity<User> updateUser(@RequestBody User userRequestBody, HttpServletRequest request) throws UserDoesNotExistException, InvalidUserUpdaRequestException {

        User userOutput=userService.updateUser(userRequestBody, (User) request.getAttribute("user"));
        return ResponseEntity.ok(userOutput);
    }


}
