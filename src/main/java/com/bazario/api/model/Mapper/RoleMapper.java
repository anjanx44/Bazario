package com.bazario.api.model.Mapper;

import com.bazario.api.model.dto.RoleDTO.RoleRequestDTO;
import com.bazario.api.model.dto.RoleDTO.RoleResponseDTO;
import com.bazario.api.model.entity.Role;

public class RoleMapper {

    public Role toEntity(RoleRequestDTO dto) {

        Role role = new Role();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());

        return role;
    }

    public RoleResponseDTO toResponseDTO(Role role) {

        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());

        return dto;
    }

}
