package com.bazario.customers.domain.model;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private UUID id;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private boolean enabled;
    private List<CustomerAddress> addresses;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
