package com.bazario.inventory.adapters.in.web;

import com.bazario.config.GlobalExceptionHandler;
import com.bazario.inventory.adapters.in.web.dto.InventoryWebMapper;
import com.bazario.inventory.ports.in.InventoryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryControllerValidationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InventoryUseCase useCase = mock(InventoryUseCase.class);
        InventoryWebMapper mapper = mock(InventoryWebMapper.class);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new InventoryController(useCase, mapper))
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void initializeWithNegativeStockReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/v1/inventory/initialize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": "%s",
                                  "initialStock": -1,
                                  "lowStockThreshold": 0
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/v1/inventory/initialize"))
                .andExpect(jsonPath("$.fieldErrors.initialStock").exists());
    }

    @Test
    void updateStockWithNullQuantityChangeReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(patch("/api/v1/inventory/" + UUID.randomUUID() + "/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "quantityChange": null }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.quantityChange").exists());
    }
}
