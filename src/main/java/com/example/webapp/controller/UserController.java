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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@Validated
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/self")
    public ResponseEntity<User> getUser(HttpServletRequest request) {

        User userOutput=(User) request.getAttribute("user");

        return ResponseEntity.ok(userOutput);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) throws UserExistsException, InvalidCreateRequest {
        User userOutput=userService.createUser(userDTO);
       return ResponseEntity.ok(userOutput);
    }

    @PutMapping("/self")
    public ResponseEntity<User> updateUser(@Valid @RequestBody UserUpdateDTO userRequestBody, HttpServletRequest request) throws UserDoesNotExistException, InvalidUserUpdaRequestException {

        User userOutput=userService.updateUser(userRequestBody, (User) request.getAttribute("user"));
        return ResponseEntity.status(204).body(userOutput);
    }


}
