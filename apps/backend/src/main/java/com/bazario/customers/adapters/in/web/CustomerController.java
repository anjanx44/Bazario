package com.bazario.customers.adapters.in.web;

import com.bazario.customers.adapters.in.web.dto.CustomerDtos;
import com.bazario.customers.adapters.in.web.dto.CustomerWebMapper;
import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.domain.model.CustomerAddress;
import com.bazario.customers.ports.in.CustomerUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerUseCase customerUseCase;
    private final CustomerWebMapper webMapper;

    @PostMapping("/register")
    public ResponseEntity<CustomerDtos.CustomerResponse> register(
            @RequestBody CustomerDtos.RegisterRequest request) {
        Customer customer = webMapper.toDomain(request);
        UUID customerId = customerUseCase.registerCustomer(customer);
        Customer registeredCustomer = customerUseCase.getCustomerById(customerId)
                .orElseThrow(() -> new IllegalStateException("Customer not found after register: " + customerId));
        return ResponseEntity.ok(webMapper.toResponse(registeredCustomer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDtos.CustomerResponse> getById(@PathVariable UUID id) {
        return customerUseCase.getCustomerById(id)
                .map(customer -> ResponseEntity.ok(webMapper.toResponse(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<CustomerDtos.AddressResponse> addAddress(
            @PathVariable UUID id,
            @RequestBody CustomerDtos.AddressRequest request) {
        CustomerAddress address = webMapper.toAddressDomain(request);
        CustomerAddress savedAddress = customerUseCase.addAddress(id, address);
        return ResponseEntity.ok(webMapper.toAddressResponse(savedAddress));
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<CustomerDtos.AddressResponse>> getAddresses(@PathVariable UUID id) {
        List<CustomerAddress> addresses = customerUseCase.getAddressesByCustomerId(id);
        return ResponseEntity.ok(webMapper.toAddressResponseList(addresses));
    }
}
