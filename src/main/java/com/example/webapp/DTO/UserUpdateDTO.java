package com.example.webapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    private String id;

    private String firstName;

    private String lastName;

    private String userName;

    private String password;

    private LocalDateTime accountCreated;

    private LocalDateTime accountUpdated;
}
