package com.example.webapp.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @Size(min = 2, max = 30)
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String firstName;

    @Size(min = 2, max = 30)
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String lastName;

    @Email
    private String userName;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    private String password;
}
