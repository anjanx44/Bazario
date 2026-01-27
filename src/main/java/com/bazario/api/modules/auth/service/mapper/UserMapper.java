package com.bazario.api.modules.auth.service.mapper;

import com.bazario.api.modules.auth.api.dto.request.UserRequestDTO;
import com.bazario.api.modules.auth.api.dto.response.UserResponseDTO;
import com.bazario.api.modules.auth.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {RoleMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRequestDTO userRequestDTO);
}
