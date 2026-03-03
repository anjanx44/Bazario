package com.bazario.api.model.dto.UserDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class UserRequestDTO {

    //checked that It is in email format
    @NotBlank(message="Email is Required")
    @Email
    private String email;

    // Used Regex to check password pattern matching
    @NotBlank(message="Password is Required")
    @Size(min=8, message = "Password must contains at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;


    @NotBlank(message = "First Name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "First Name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String lastName;

    // Used regex to check BD mobile number pattern
    @NotBlank(message = "Mobile Number is Required")
    @Pattern(
            regexp = "^(\\+8801|01)[3-9]\\d{8}$",
            message = "Phone number must be a valid Bangladeshi number"
    )
    private String phoneNumber;

    private Set<String> roleIds;
}
