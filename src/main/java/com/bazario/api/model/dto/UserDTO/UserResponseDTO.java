package com.bazario.api.model.dto.UserDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class UserResponseDTO {

    private String id;
    private String email;

    private String first_name;
    private String last_name;
    private String phoneNumber;

    private boolean isEnabled ;
    private boolean isAccountNonLocked ;
    private boolean isAccountNonExpired ;
    private boolean isCredentialsNonExpired ;

    private Set<String> roles;
}
