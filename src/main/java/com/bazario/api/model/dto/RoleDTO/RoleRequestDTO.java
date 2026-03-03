package com.bazario.api.model.dto.RoleDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleRequestDTO {

    @NotBlank(message = "Role Name Is Required")
    private String name;
    private String description;
}
