package com.bazario.customers.adapters.in.web;

import com.bazario.config.GlobalExceptionHandler;
import com.bazario.customers.adapters.in.web.dto.CustomerDtos;
import com.bazario.customers.adapters.in.web.dto.CustomerWebMapper;
import com.bazario.customers.domain.model.CustomerAddress;
import com.bazario.customers.ports.in.CustomerUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Paging contract of {@code GET /api/v2/customers/{id}/addresses}:
 * default size, custom size, the size ceiling, and an empty page.
 */
class CustomerControllerPaginationTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();

    private final CustomerUseCase customerUseCase = mock(CustomerUseCase.class);
    private final CustomerWebMapper webMapper = mock(CustomerWebMapper.class);

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        when(webMapper.toAddressResponse(any(CustomerAddress.class))).thenReturn(
                new CustomerDtos.AddressResponse(
                        UUID.randomUUID(), CUSTOMER_ID, "1 Main St", null, "Nanjing",
                        "210000", "CN", true, null));

        mockMvc = MockMvcBuilders.standaloneSetup(new CustomerPaginationController(customerUseCase, webMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private void stubAddressesPage(long totalElements) {
        when(customerUseCase.getAddressesByCustomerId(eq(CUSTOMER_ID), any(Pageable.class)))
                .thenAnswer(invocation -> new PageImpl<>(
                        List.of(CustomerAddress.builder().customerId(CUSTOMER_ID).build()),
                        invocation.getArgument(1),
                        totalElements));
    }

    @Test
    void defaultsToFirstPageOfTwentyNewestFirst() throws Exception {
        stubAddressesPage(41);

        mockMvc.perform(get("/api/v2/customers/{id}/addresses", CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(41))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.content", hasSize(1)));

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(customerUseCase).getAddressesByCustomerId(eq(CUSTOMER_ID), pageable.capture());
        assertThat(pageable.getValue().getPageSize()).isEqualTo(20);
        assertThat(pageable.getValue().getSort().getOrderFor("createdAt").getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void honoursAnExplicitPageAndSize() throws Exception {
        stubAddressesPage(41);

        mockMvc.perform(get("/api/v2/customers/{id}/addresses", CUSTOMER_ID)
                        .param("page", "2")
                        .param("size", "10")
                        .param("sort", "city,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(5));

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(customerUseCase).getAddressesByCustomerId(eq(CUSTOMER_ID), pageable.capture());
        assertThat(pageable.getValue().getSort().getOrderFor("city").getDirection())
                .isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void clampsSizeAtOneHundred() throws Exception {
        stubAddressesPage(250);

        mockMvc.perform(get("/api/v2/customers/{id}/addresses", CUSTOMER_ID)
                        .param("size", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(100))
                .andExpect(jsonPath("$.totalPages").value(3));

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(customerUseCase).getAddressesByCustomerId(eq(CUSTOMER_ID), pageable.capture());
        assertThat(pageable.getValue().getPageSize()).isEqualTo(100);
    }

    @Test
    void returnsAnEmptyPageWithZeroTotals() throws Exception {
        when(customerUseCase.getAddressesByCustomerId(eq(CUSTOMER_ID), any(Pageable.class)))
                .thenAnswer(invocation -> new PageImpl<CustomerAddress>(
                        List.of(), invocation.getArgument(1), 0));

        mockMvc.perform(get("/api/v2/customers/{id}/addresses", CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void rejectsASizeBelowOne() throws Exception {
        mockMvc.perform(get("/api/v2/customers/{id}/addresses", CUSTOMER_ID)
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("size")));
    }
}
