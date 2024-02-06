package com.example.webapp.service;

import com.example.webapp.DAO.UserDAO;
import com.example.webapp.DTO.UserDTO;
import com.example.webapp.DTO.UserUpdateDTO;
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

    public User createUser(UserDTO userDTO) throws UserExistsException {
        if(ObjectUtils.isEmpty(userDTO.getUserName()) || ObjectUtils.isEmpty(userDTO.getFirstName()) || ObjectUtils.isEmpty(userDTO.getLastName()) || ObjectUtils.isEmpty(userDTO.getPassword())) throw new IllegalArgumentException();
        Optional<User> userTest=userDAO.findUserByUserNameIgnoreCase(userDTO.getUserName());
        if(userTest.isPresent()) throw new UserExistsException();
        String encodedPassword=passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);
        User user=modelMapper.map(userDTO,User.class);
        user.setAccountCreated(LocalDateTime.now());
        user.setAccountUpdated(user.getAccountCreated());
        return userDAO.save(user);

    }

    public User getUser(String userName) throws UserDoesNotExistException {
        Optional<User> userTest=userDAO.findUserByUserNameIgnoreCase(userName);
        if(userTest.isEmpty()) throw new UserDoesNotExistException();
        return userTest.get();
    }

    public User updateUser(UserUpdateDTO userRequestBody, User user) throws InvalidUserUpdaRequestException {
        if(userRequestBody!=null && (userRequestBody.getId()!=null || userRequestBody.getAccountCreated()!=null || userRequestBody.getAccountUpdated()!=null ||userRequestBody.getUserName()!=null)) throw new InvalidUserUpdaRequestException();

        if(userRequestBody!=null && userRequestBody.getFirstName()!=null) user.setFirstName(userRequestBody.getFirstName());

        if(userRequestBody!=null && userRequestBody.getLastName()!=null) user.setLastName(userRequestBody.getLastName());

        if(userRequestBody!=null && userRequestBody.getPassword()!=null) user.setPassword(passwordEncoder.encode(userRequestBody.getPassword()));

        user.setAccountUpdated(LocalDateTime.now());

        return userDAO.save(user);

    }
}
