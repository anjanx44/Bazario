package com.bazario.customers.adapters.in.web.dto;

import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.domain.model.CustomerAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Customer toDomain(CustomerDtos.RegisterRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CustomerAddress toAddressDomain(CustomerDtos.AddressRequest request);

    CustomerDtos.CustomerResponse toResponse(Customer domain);

    CustomerDtos.AddressResponse toAddressResponse(CustomerAddress domain);

    List<CustomerDtos.AddressResponse> toAddressResponseList(List<CustomerAddress> domains);
}
