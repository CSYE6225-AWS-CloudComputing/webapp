package com.example.webapp.service;

import com.example.webapp.DAO.UserDAO;
import com.example.webapp.controlleradvice.InvalidUserUpdaRequestException;
import com.example.webapp.controlleradvice.UserDoesNotExistException;
import com.example.webapp.controlleradvice.UserExistsException;
import com.example.webapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) throws UserExistsException {

        Optional<User> userTest=userDAO.findUserByUserNameIgnoreCase(user.getUserName());
        if(userTest.isPresent()) throw new UserExistsException();
        String encodedPassword=passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setAccountCreated(null);
        user.setAccountUpdated(null);

        return userDAO.save(user);

    }

    public User getUser(String userName) throws UserDoesNotExistException {
        Optional<User> userTest=userDAO.findUserByUserNameIgnoreCase(userName);
        if(userTest.isEmpty()) throw new UserDoesNotExistException();
        return userDAO.findUserByUserNameIgnoreCase(userName).get();
    }

    public User updateUser(User userRequestBody, User user) throws UserDoesNotExistException, InvalidUserUpdaRequestException {
        if(userRequestBody!=null && (userRequestBody.getId()!=null || userRequestBody.getAccountCreated()!=null || userRequestBody.getAccountUpdated()!=null ||userRequestBody.getUserName()!=null)) throw new InvalidUserUpdaRequestException();
        Optional<User> userTest=userDAO.findUserByUserNameIgnoreCase(user.getUserName());
        if(userTest.isEmpty()) throw new UserDoesNotExistException();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDAO.save(user);

    }
}
