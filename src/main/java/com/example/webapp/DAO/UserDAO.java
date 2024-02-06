package com.example.webapp.DAO;

import com.example.webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User,Long> {

    Optional<User> findUserByUserNameIgnoreCase(String userName);
}
