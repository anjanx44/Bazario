package com.bazario.customers.adapters.in.web;

import com.bazario.config.GlobalExceptionHandler;
import com.bazario.customers.adapters.in.web.dto.CustomerWebMapper;
import com.bazario.customers.ports.in.CustomerUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerControllerValidationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CustomerUseCase useCase = mock(CustomerUseCase.class);
        CustomerWebMapper mapper = mock(CustomerWebMapper.class);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new CustomerController(useCase, mapper))
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerWithBlankEmailReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/v1/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "password": "password1",
                                  "firstName": "Ada",
                                  "lastName": "Lovelace",
                                  "phone": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/v1/customers/register"))
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void addAddressWithBlankCityReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/v1/customers/" + UUID.randomUUID() + "/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "addressLine1": "1 Main St",
                                  "city": "",
                                  "postalCode": "E1 1AA",
                                  "country": "UK",
                                  "isDefault": false
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.city").exists());
    }
}
