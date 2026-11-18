package com.bazario.inventory.adapters.in.web.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryDtosValidationTest {

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
    void updateStockRejectsNullQuantityChange() {
        assertThat(violatedFields(new InventoryDtos.UpdateStockRequest(null)))
                .contains("quantityChange");
    }

    @Test
    void updateStockAcceptsZeroAndNegativeChanges() {
        assertThat(violatedFields(new InventoryDtos.UpdateStockRequest(0))).isEmpty();
        assertThat(violatedFields(new InventoryDtos.UpdateStockRequest(-5))).isEmpty();
    }

    @Test
    void initializeRejectsNullProductId() {
        var request = new InventoryDtos.InitializeInventoryRequest(null, 0, 0);
        assertThat(violatedFields(request)).contains("productId");
    }

    @Test
    void initializeRejectsNullStockValues() {
        var request = new InventoryDtos.InitializeInventoryRequest(UUID.randomUUID(), null, null);
        assertThat(violatedFields(request)).contains("initialStock", "lowStockThreshold");
    }

    @Test
    void initializeRejectsNegativeStockValues() {
        var request = new InventoryDtos.InitializeInventoryRequest(UUID.randomUUID(), -1, -2);
        assertThat(violatedFields(request)).contains("initialStock", "lowStockThreshold");
    }

    @Test
    void initializeAcceptsZeroStockValues() {
        var request = new InventoryDtos.InitializeInventoryRequest(UUID.randomUUID(), 0, 0);
        assertThat(violatedFields(request)).isEmpty();
    }
}
