package com.bazario.orders.adapters.in.web;

import com.bazario.config.GlobalExceptionHandler;
import com.bazario.orders.adapters.in.web.dto.OrderDtos;
import com.bazario.orders.adapters.in.web.dto.OrderWebMapper;
import com.bazario.orders.domain.model.Order;
import com.bazario.orders.domain.model.OrderStatus;
import com.bazario.orders.ports.in.OrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The legacy contract must not change: {@code GET /api/v1/orders/customer/{id}}
 * still answers with a plain JSON array, not a page envelope.
 */
class OrderControllerLegacyListTest {

    private static final UUID CUSTOMER_ID = UUID.randomUUID();

    private final OrderUseCase orderUseCase = mock(OrderUseCase.class);
    private final OrderWebMapper webMapper = mock(OrderWebMapper.class);

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        OrderDtos.OrderResponse response = new OrderDtos.OrderResponse(
                UUID.randomUUID(), "ORD-1", CUSTOMER_ID, new BigDecimal("10.00"),
                OrderStatus.PENDING, "somewhere", List.of(), null, null);

        when(orderUseCase.getOrdersByCustomerId(eq(CUSTOMER_ID)))
                .thenReturn(List.of(Order.builder().id(CUSTOMER_ID).build()));
        when(webMapper.toResponseList(anyList())).thenReturn(List.of(response));

        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderUseCase, webMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsAPlainArrayWithoutAnEnvelope() throws Exception {
        mockMvc.perform(get("/api/v1/orders/customer/{customerId}", CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderNumber").value("ORD-1"))
                .andExpect(jsonPath("$.content").doesNotExist());

        verify(orderUseCase).getOrdersByCustomerId(CUSTOMER_ID);
    }
}
