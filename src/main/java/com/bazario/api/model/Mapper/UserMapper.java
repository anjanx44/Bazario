package com.bazario.api.model.Mapper;

import com.bazario.api.model.dto.UserDTO.UserRequestDTO;
import com.bazario.api.model.dto.UserDTO.UserResponseDTO;
import com.bazario.api.model.entity.Role;
import com.bazario.api.model.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public User toEntity(UserRequestDTO dto){

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());

        return user;
    }


    public UserResponseDTO toResponseDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());

        dto.setFirst_name(user.getFirstName());
        dto.setLast_name(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());

        dto.setEnabled(user.isEnabled());
        dto.setAccountNonLocked(user.isAccountNonLocked());
        dto.setAccountNonExpired(user.isAccountNonExpired());
        dto.setCredentialsNonExpired(user.isCredentialsNonExpired());

        if (user.getRoles() != null) {
            Set<String> roles = user.getRoles()
                    .stream()
                    .map(Role::getName) // অথবা Role::getId
                    .collect(Collectors.toSet());
            dto.setRoles(roles);
        }

        return dto;
    }



}
