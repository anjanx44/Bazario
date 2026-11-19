package com.bazario.orders.adapters.in.web.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDtosValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        factory.close();
    }

    private static Set<String> violatedFields(Object request) {
        return validator.validate(request).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void createOrderRejectsNullCustomerId() {
        var request = new OrderDtos.CreateOrderRequest(null, List.of(
                new OrderDtos.OrderItemRequest(UUID.randomUUID(), 1)));
        assertThat(violatedFields(request)).contains("customerId");
    }

    @Test
    void createOrderRejectsEmptyItems() {
        var request = new OrderDtos.CreateOrderRequest(UUID.randomUUID(), List.of());
        assertThat(violatedFields(request)).contains("items");
    }

    @Test
    void orderItemRejectsNullProductId() {
        var request = new OrderDtos.OrderItemRequest(null, 1);
        assertThat(violatedFields(request)).contains("productId");
    }

    @Test
    void orderItemRejectsNullQuantity() {
        var request = new OrderDtos.OrderItemRequest(UUID.randomUUID(), null);
        assertThat(violatedFields(request)).contains("quantity");
    }

    @Test
    void orderItemRejectsZeroAndNegativeQuantity() {
        assertThat(violatedFields(new OrderDtos.OrderItemRequest(UUID.randomUUID(), 0)))
                .contains("quantity");
        assertThat(violatedFields(new OrderDtos.OrderItemRequest(UUID.randomUUID(), -3)))
                .contains("quantity");
    }

    @Test
    void createOrderCascadesValidationIntoItems() {
        var request = new OrderDtos.CreateOrderRequest(UUID.randomUUID(), List.of(
                new OrderDtos.OrderItemRequest(UUID.randomUUID(), -1)));
        assertThat(violatedFields(request)).anySatisfy(path ->
                assertThat(path).startsWith("items[0].quantity"));
    }

    @Test
    void createOrderAcceptsValidRequest() {
        var request = new OrderDtos.CreateOrderRequest(UUID.randomUUID(), List.of(
                new OrderDtos.OrderItemRequest(UUID.randomUUID(), 2)));
        assertThat(violatedFields(request)).isEmpty();
    }
}
