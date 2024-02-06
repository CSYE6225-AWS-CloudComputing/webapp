package com.example.webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "webapp_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private String id;

    @JsonProperty
    @Column(nullable = false)
    private String firstName;

    @JsonProperty
    @Column(nullable = false)
    private String lastName;

    @JsonProperty
    @Column(nullable = false,updatable = false)
    private String userName;

    @JsonProperty
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL", updatable = false)
    private LocalDateTime accountCreated;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",nullable = false)
    private LocalDateTime accountUpdated;

}
