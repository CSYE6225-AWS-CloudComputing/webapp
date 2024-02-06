package com.example.webapp.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String firstName;

    private String lastName;

    private String userName;

    @JsonIgnore
    private String password;
}
