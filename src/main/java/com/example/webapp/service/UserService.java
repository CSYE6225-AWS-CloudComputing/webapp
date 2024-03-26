package com.example.webapp.service;

import com.example.webapp.DAO.EmailLogDAO;
import com.example.webapp.DAO.UserDAO;
import com.example.webapp.DTO.UserDTO;
import com.example.webapp.DTO.UserUpdateDTO;
import com.example.webapp.controller.UserController;
import com.example.webapp.controlleradvice.InvalidCreateRequest;
import com.example.webapp.controlleradvice.InvalidUserUpdaRequestException;
import com.example.webapp.controlleradvice.UserDoesNotExistException;
import com.example.webapp.controlleradvice.UserExistsException;
import com.example.webapp.model.EmailLog;
import com.example.webapp.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private PubSubService pubSubService;

    @Autowired
    UserDAO userDAO;

    @Autowired
    EmailLogDAO emailLogDAO;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Logger logger = LogManager.getLogger(UserService.class);

    public User createUser(UserDTO userDTO) throws UserExistsException, InvalidCreateRequest {
        if (ObjectUtils.isEmpty(userDTO.getUserName())) {
            throw new InvalidCreateRequest("Username is required");
        }

        if (ObjectUtils.isEmpty(userDTO.getFirstName())) {
            throw new InvalidCreateRequest("First name is required");
        }

        if (ObjectUtils.isEmpty(userDTO.getLastName())) {
            throw new InvalidCreateRequest("Last name is required");
        }

        if (ObjectUtils.isEmpty(userDTO.getPassword())) {
            throw new InvalidCreateRequest("Password is required");
        }
        Optional<User> userTest=userDAO.findUserByUserNameIgnoreCase(userDTO.getUserName());
        if(userTest.isPresent()) throw new UserExistsException("User Already Exists");
        String encodedPassword=passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);
        User user=modelMapper.map(userDTO,User.class);
        user.setAccountCreated(LocalDateTime.now());
        user.setAccountUpdated(user.getAccountCreated());
        user.setAuthenticated(false);
        return userDAO.save(user);
    }

    public boolean verifyToken(String receivedToken) {
        try {
            // Decode the received token from Base64
            byte[] decodedBytes = Base64.getDecoder().decode(receivedToken);
            // Convert the decoded bytes to a string
            String decodedToken = new String(decodedBytes);
            // Split the decoded token into email and timestamp
            String[] parts = decodedToken.split(":");
            // Extract email and timestamp
            String email = parts[0];
            long timestamp = Long.parseLong(parts[1]);

            long currentTime = new Date().getTime();
            long expiryTime = currentTime - (2 * 60 * 1000);
            Optional<User> userOutput=userDAO.findUserByUserNameIgnoreCase(email);
            if(userOutput.isEmpty()) return false;
            User updateUser=userOutput.get();
            if(timestamp >= expiryTime){
                updateUser.setAuthenticated(true);
                userDAO.save(updateUser);
                return true;
            }
            Optional<EmailLog> emailLogOptional=emailLogDAO.findEmailLogByUserEmailIgnoreCase(email);
            emailLogOptional.ifPresent(emailLog -> logger.info("Email Log entry: " + emailLog.toString()));
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // If any error occurs during decoding or validation, consider the token invalid
        }
    }

    public User getUser(String userName) throws UserDoesNotExistException {
        Optional<User> userTest=userDAO.findUserByUserNameIgnoreCase(userName);
        if(userTest.isEmpty()) throw new UserDoesNotExistException("User does not exist");
        return userTest.get();
    }

    public User updateUser(UserUpdateDTO userRequestBody, User user) throws InvalidUserUpdaRequestException {
        if (userRequestBody != null) {
            if (userRequestBody.getId() != null || userRequestBody.getAccountCreated() != null || userRequestBody.getAccountUpdated() != null || userRequestBody.getUserName() != null) {
                throw new InvalidUserUpdaRequestException("Cannot update ID or account_created date or account_updated date or userName");
            }
        }
        if(userRequestBody!=null && userRequestBody.getFirstName()!=null){
            user.setFirstName(userRequestBody.getFirstName());
        }else{
            throw new InvalidUserUpdaRequestException("first name missing");
        }

        if(userRequestBody!=null && userRequestBody.getLastName()!=null){
            user.setLastName(userRequestBody.getLastName());
        }else{
            throw new InvalidUserUpdaRequestException("last name missing");
        }

        if(userRequestBody!=null && userRequestBody.getPassword()!=null){
            user.setPassword(passwordEncoder.encode(userRequestBody.getPassword()));
        }else{
            throw new InvalidUserUpdaRequestException("password missing");
        }

        user.setAccountUpdated(LocalDateTime.now());

        return userDAO.save(user);

    }
}
