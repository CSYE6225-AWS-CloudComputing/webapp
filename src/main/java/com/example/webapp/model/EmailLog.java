package com.example.webapp.model;

import jakarta.persistence.*;
import lombok.*;

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

}
