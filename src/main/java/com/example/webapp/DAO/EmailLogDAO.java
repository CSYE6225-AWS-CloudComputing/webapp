package com.example.webapp.DAO;

import com.example.webapp.model.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailLogDAO extends JpaRepository<EmailLog, Long> {

    Optional<EmailLog> findEmailLogByUserEmailIgnoreCase(String email);
}
