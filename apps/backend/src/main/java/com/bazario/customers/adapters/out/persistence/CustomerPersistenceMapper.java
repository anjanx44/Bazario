package com.bazario.customers.adapters.out.persistence;

import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.domain.model.CustomerAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerPersistenceMapper {

    Customer toDomain(CustomerJpaEntity entity);

    @Mapping(target = "addresses", ignore = true)
    CustomerJpaEntity toEntity(Customer domain);

    @Mapping(target = "customerId", source = "customer.id")
    CustomerAddress toAddressDomain(CustomerAddressJpaEntity entity);

    @Mapping(target = "customer", ignore = true)
    CustomerAddressJpaEntity toAddressEntity(CustomerAddress domain);

    List<CustomerAddress> toAddressDomainList(List<CustomerAddressJpaEntity> entities);
}
