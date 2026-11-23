package com.bazario.orders.adapters.in.web;

import com.bazario.config.GlobalExceptionHandler;
import com.bazario.orders.adapters.in.web.dto.OrderDtos;
import com.bazario.orders.adapters.in.web.dto.OrderWebMapper;
import com.bazario.orders.domain.model.Order;
import com.bazario.orders.domain.model.OrderStatus;
import com.bazario.orders.ports.in.OrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
 * Paging contract of {@code GET /api/v1/orders/customer/{customerId}}:
 * default size, custom size, the size ceiling, and an empty page.
 */
class OrderControllerPaginationTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();

    private final OrderUseCase orderUseCase = mock(OrderUseCase.class);
    private final OrderWebMapper webMapper = mock(OrderWebMapper.class);

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        when(webMapper.toResponse(any(Order.class))).thenReturn(new OrderDtos.OrderResponse(
                UUID.randomUUID(), "ORD-1", CUSTOMER_ID, new BigDecimal("10.00"),
                OrderStatus.PENDING, "somewhere", List.of(), null, null));

        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderUseCase, webMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private void stubOrdersPage(long totalElements) {
        when(orderUseCase.getOrdersByCustomerId(eq(CUSTOMER_ID), any(Pageable.class)))
                .thenAnswer(invocation -> new PageImpl<>(
                        List.of(Order.builder().id(CUSTOMER_ID).build()),
                        invocation.getArgument(1),
                        totalElements));
    }

    @Test
    void defaultsToFirstPageOfTwentyNewestFirst() throws Exception {
        stubOrdersPage(42);

        mockMvc.perform(get("/api/v1/orders/customer/{customerId}", CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(42))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.content", hasSize(1)));

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(orderUseCase).getOrdersByCustomerId(eq(CUSTOMER_ID), pageable.capture());
        assertThat(pageable.getValue().getPageNumber()).isZero();
        assertThat(pageable.getValue().getPageSize()).isEqualTo(20);
        assertThat(pageable.getValue().getSort().getOrderFor("createdAt").getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void honoursAnExplicitPageAndSize() throws Exception {
        stubOrdersPage(42);

        mockMvc.perform(get("/api/v1/orders/customer/{customerId}", CUSTOMER_ID)
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "totalAmount,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPages").value(9));

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(orderUseCase).getOrdersByCustomerId(eq(CUSTOMER_ID), pageable.capture());
        assertThat(pageable.getValue().getPageNumber()).isEqualTo(1);
        assertThat(pageable.getValue().getPageSize()).isEqualTo(5);
        assertThat(pageable.getValue().getSort().getOrderFor("totalAmount").getDirection())
                .isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void clampsSizeAtOneHundred() throws Exception {
        stubOrdersPage(250);

        mockMvc.perform(get("/api/v1/orders/customer/{customerId}", CUSTOMER_ID)
                        .param("size", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(100))
                .andExpect(jsonPath("$.totalPages").value(3));

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(orderUseCase).getOrdersByCustomerId(eq(CUSTOMER_ID), pageable.capture());
        assertThat(pageable.getValue().getPageSize()).isEqualTo(100);
    }

    @Test
    void returnsAnEmptyPageWithZeroTotals() throws Exception {
        when(orderUseCase.getOrdersByCustomerId(eq(CUSTOMER_ID), any(Pageable.class)))
                .thenAnswer(invocation -> new PageImpl<Order>(List.of(), invocation.getArgument(1), 0));

        mockMvc.perform(get("/api/v1/orders/customer/{customerId}", CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void rejectsAnUnknownSortProperty() throws Exception {
        mockMvc.perform(get("/api/v1/orders/customer/{customerId}", CUSTOMER_ID)
                        .param("sort", "shippingAddressSnapshot,desc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("Unsupported sort property")));
    }
}
