package com.bazario.customers.ports.in;

import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.domain.model.CustomerAddress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerUseCase {
    Customer registerCustomer(Customer customer);
    Optional<Customer> getCustomerById(UUID id);
    Optional<Customer> getCustomerByEmail(String email);
    CustomerAddress addAddress(UUID customerId, CustomerAddress address);
    List<CustomerAddress> getAddressesByCustomerId(UUID customerId);
}
