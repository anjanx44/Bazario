package com.bazario.customers.adapters.in.web;

import com.bazario.customers.adapters.in.web.dto.CustomerDtos;
import com.bazario.customers.adapters.in.web.dto.CustomerWebMapper;
import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.domain.model.CustomerAddress;
import com.bazario.customers.ports.in.CustomerUseCase;
import com.bazario.shared.web.PageRequests;
import com.bazario.shared.web.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    /** Address fields that may be used as the {@code sort} query parameter. */
    private static final Set<String> ADDRESS_SORTABLE_PROPERTIES =
            Set.of("createdAt", "city", "postalCode", "country");

    private final CustomerUseCase customerUseCase;
    private final CustomerWebMapper webMapper;

    @PostMapping("/register")
    public ResponseEntity<CustomerDtos.CustomerResponse> register(
            @Valid @RequestBody CustomerDtos.RegisterRequest request) {
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
            @Valid @RequestBody CustomerDtos.AddressRequest request) {
        CustomerAddress address = webMapper.toAddressDomain(request);
        CustomerAddress savedAddress = customerUseCase.addAddress(id, address);
        return ResponseEntity.ok(webMapper.toAddressResponse(savedAddress));
    }

    /**
     * Paginated address list.
     *
     * Query parameters: {@code page} (default 0), {@code size} (default 20, max 100),
     * {@code sort} (default {@code createdAt,desc}).
     * Response: {@code {content[], page, size, totalElements, totalPages}}.
     */
    @GetMapping("/{id}/addresses")
    public ResponseEntity<PageResponse<CustomerDtos.AddressResponse>> getAddresses(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        Pageable pageable = PageRequests.of(page, size, sort, ADDRESS_SORTABLE_PROPERTIES);
        Page<CustomerAddress> addresses = customerUseCase.getAddressesByCustomerId(id, pageable);
        return ResponseEntity.ok(PageResponse.of(addresses.map(webMapper::toAddressResponse)));
    }
}
