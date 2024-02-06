package com.example.webapp.controller;

import com.example.webapp.DTO.UserDTO;
import com.example.webapp.DTO.UserUpdateDTO;
import com.example.webapp.controlleradvice.InvalidUserUpdaRequestException;
import com.example.webapp.controlleradvice.UserDoesNotExistException;
import com.example.webapp.controlleradvice.UserExistsException;
import com.example.webapp.model.User;
import com.example.webapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<User> createUser(@RequestBody UserDTO UserDTO) throws UserExistsException {
        User userOutput=userService.createUser(UserDTO);
       return ResponseEntity.ok(userOutput);
    }

    @PutMapping("/self")
    public ResponseEntity<User> updateUser(@RequestBody UserUpdateDTO userRequestBody, HttpServletRequest request) throws UserDoesNotExistException, InvalidUserUpdaRequestException {

        User userOutput=userService.updateUser(userRequestBody, (User) request.getAttribute("user"));
        return ResponseEntity.ok(userOutput);
    }


}
