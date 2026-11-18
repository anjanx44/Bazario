package com.bazario.customers.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class CustomerDtos {

    public record RegisterRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8) String password,
            @NotBlank String firstName,
            String lastName,
            String phone
    ) {}

    public record AddressRequest(
            @NotBlank String addressLine1,
            String addressLine2,
            @NotBlank String city,
            @NotBlank String postalCode,
            @NotBlank String country,
            boolean isDefault
    ) {}

    public record CustomerResponse(
            UUID id,
            String email,
            String firstName,
            String lastName,
            String phone,
            boolean enabled,
            List<AddressResponse> addresses,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt
    ) {}

    public record AddressResponse(
            UUID id,
            UUID customerId,
            String addressLine1,
            String addressLine2,
            String city,
            String postalCode,
            String country,
            boolean isDefault,
            ZonedDateTime createdAt
    ) {}
}
