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

    /**
     * Atomically replaces the password hash only while it still matches
     * {@code expectedPasswordHash} and the customer is enabled.
     *
     * @return {@code true} if the row was updated; {@code false} if the password
     *         changed in the meantime or the account is disabled
     */
    boolean updatePasswordIfUnchanged(UUID customerId, String expectedPasswordHash, String newPasswordHash);
    CustomerAddress saveAddress(CustomerAddress address);
    List<CustomerAddress> findAddressesByCustomerId(UUID customerId);
}
