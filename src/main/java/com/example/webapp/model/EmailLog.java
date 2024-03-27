package com.example.webapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailLog {

    @Id
    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    boolean isEmailSent;

    @Column(nullable = false)
    String userEmail;

    @Column(updatable = false)
    private LocalDateTime timeStamp;

    @Column(nullable = false)
    private String verificationLink;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    @Override
    public String toString() {
        return "EmailLog{" +
                "id='" + id + '\'' +
                ", isEmailSent=" + isEmailSent +
                ", userEmail='" + userEmail + '\'' +
                ", timeStamp=" + timeStamp +
                ", verificationLink='" + verificationLink + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }

}
