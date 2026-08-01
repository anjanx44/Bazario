package com.bazario.customers.domain.service;

import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.domain.model.CustomerAddress;
import com.bazario.customers.ports.in.CustomerUseCase;
import com.bazario.customers.ports.out.CustomerRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService implements CustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Customer registerCustomer(Customer customer) {
        log.info("Registering new customer with email: {}", customer.getEmail());

        if (customerRepositoryPort.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Customer with email " + customer.getEmail() + " already exists");
        }

        // Hash password
        customer.setPasswordHash(passwordEncoder.encode(customer.getPasswordHash()));
        
        customer.setEnabled(true);
        customer.setCreatedAt(ZonedDateTime.now());
        customer.setUpdatedAt(ZonedDateTime.now());

        return customerRepositoryPort.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(UUID id) {
        return customerRepositoryPort.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepositoryPort.findByEmail(email);
    }

    @Override
    public CustomerAddress addAddress(UUID customerId, CustomerAddress address) {
        log.info("Adding new address for customer: {}", customerId);
        
        // Ensure customer exists
        customerRepositoryPort.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        address.setCustomerId(customerId);
        address.setCreatedAt(ZonedDateTime.now());

        return customerRepositoryPort.saveAddress(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerAddress> getAddressesByCustomerId(UUID customerId) {
        return customerRepositoryPort.findAddressesByCustomerId(customerId);
    }
}
