package com.bazario.customers.adapters.out.persistence;

import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.domain.model.CustomerAddress;
import com.bazario.customers.ports.out.CustomerRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostgresCustomerAdapter implements CustomerRepositoryPort {

    private final SpringDataCustomerRepository customerRepository;
    private final SpringDataCustomerAddressRepository addressRepository;
    private final CustomerPersistenceMapper mapper;

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity entity = mapper.toEntity(customer);
        CustomerJpaEntity savedEntity = customerRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return customerRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public CustomerAddress saveAddress(CustomerAddress address) {
        CustomerAddressJpaEntity entity = mapper.toAddressEntity(address);
        
        // Link to customer entity
        CustomerJpaEntity customerEntity = customerRepository.findById(address.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + address.getCustomerId()));
        entity.setCustomer(customerEntity);
        
        CustomerAddressJpaEntity savedEntity = addressRepository.save(entity);
        return mapper.toAddressDomain(savedEntity);
    }

    @Override
    public List<CustomerAddress> findAddressesByCustomerId(UUID customerId) {
        List<CustomerAddressJpaEntity> entities = addressRepository.findByCustomerId(customerId);
        return mapper.toAddressDomainList(entities);
    }
}
