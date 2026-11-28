package com.bazario.customers.adapters.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataCustomerAddressRepository extends JpaRepository<CustomerAddressJpaEntity, UUID> {
    List<CustomerAddressJpaEntity> findByCustomerId(UUID customerId);

    Page<CustomerAddressJpaEntity> findByCustomerId(UUID customerId, Pageable pageable);
}
