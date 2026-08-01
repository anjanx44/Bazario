package com.bazario.customers.ports.out;

import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.domain.model.CustomerAddress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepositoryPort {
    Customer save(Customer customer);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findById(UUID id);
    CustomerAddress saveAddress(CustomerAddress address);
    List<CustomerAddress> findAddressesByCustomerId(UUID customerId);
}
