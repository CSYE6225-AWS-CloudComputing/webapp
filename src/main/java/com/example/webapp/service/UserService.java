package com.example.webapp.service;

import com.example.webapp.DAO.UserDAO;
import com.example.webapp.DTO.UserDTO;
import com.example.webapp.DTO.UserUpdateDTO;
import com.example.webapp.controlleradvice.InvalidCreateRequest;
import com.example.webapp.controlleradvice.InvalidUserUpdaRequestException;
import com.example.webapp.controlleradvice.UserDoesNotExistException;
import com.example.webapp.controlleradvice.UserExistsException;
import com.example.webapp.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        return userDAO.save(user);

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
