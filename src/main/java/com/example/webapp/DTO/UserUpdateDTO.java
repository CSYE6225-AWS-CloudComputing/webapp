package com.example.webapp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    private String id;

    @JsonProperty("first_name")
    @Size(min = 2, max = 30, message = "Size must be between 2 and 30")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Must only contain letters")
    private String firstName;

    @JsonProperty("last_name")
    @Size(min = 2, max = 30, message = "Size must be between 2 and 30")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Must only contain letters")
    private String lastName;

    private String userName;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Must contain at least 8 characters, including one lowercase letter, one uppercase letter, one digit, and one special character (@$!%*?&)")
    private String password;

    @JsonProperty("account_created")
    private LocalDateTime accountCreated;

    @JsonProperty("account_updated")
    private LocalDateTime accountUpdated;

    public UserUpdateDTO(String firstName, String lastName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

}
