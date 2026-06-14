package com.bazario.customers.adapters.in.web.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class CustomerDtos {

    public record RegisterRequest(
            String email,
            String password,
            String firstName,
            String lastName,
            String phone
    ) {}

    public record AddressRequest(
            String addressLine1,
            String addressLine2,
            String city,
            String postalCode,
            String country,
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
