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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private String id;

    @JsonProperty("first_name")
    @Column(nullable = false)
    private String firstName;

    @JsonProperty("last_name")
    @Column(nullable = false)
    private String lastName;

    @JsonProperty
    @Column(nullable = false,updatable = false)
    private String userName;

    @JsonProperty
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @JsonProperty("account_created")
    @Column(updatable = false)
    private LocalDateTime accountCreated;

    @JsonProperty("account_updated")
    @Column(nullable = false)
    private LocalDateTime accountUpdated;

    @JsonIgnore
    boolean isAuthenticated;

    public void setIsAuthenticated(boolean flag){
        isAuthenticated=flag;
    }
    public User(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
    }

}
