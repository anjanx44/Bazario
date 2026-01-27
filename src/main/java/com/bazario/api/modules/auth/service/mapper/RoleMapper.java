package com.bazario.api.modules.auth.service.mapper;

import com.bazario.api.modules.auth.api.dto.request.RoleRequestDTO;
import com.bazario.api.modules.auth.api.dto.response.RoleResponseDTO;
import com.bazario.api.modules.auth.domain.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RoleMapper {
    RoleResponseDTO toResponseDTO(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Role toEntity(RoleRequestDTO roleRequestDTO);
}
