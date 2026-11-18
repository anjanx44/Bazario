package com.bazario.customers.adapters.in.web.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerDtosValidationTest {

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
    void registerRejectsBlankEmail() {
        var request = new CustomerDtos.RegisterRequest("  ", "password1", "Ada", "Lovelace", "123");
        assertThat(violatedFields(request)).contains("email");
    }

    @Test
    void registerRejectsMalformedEmail() {
        var request = new CustomerDtos.RegisterRequest("not-an-email", "password1", "Ada", "Lovelace", "123");
        assertThat(violatedFields(request)).contains("email");
    }

    @Test
    void registerRejectsBlankPassword() {
        var request = new CustomerDtos.RegisterRequest("ada@example.com", "  ", "Ada", "Lovelace", "123");
        assertThat(violatedFields(request)).contains("password");
    }

    @Test
    void registerRejectsShortPassword() {
        var request = new CustomerDtos.RegisterRequest("ada@example.com", "short", "Ada", "Lovelace", "123");
        assertThat(violatedFields(request)).contains("password");
    }

    @Test
    void registerRejectsBlankFirstName() {
        var request = new CustomerDtos.RegisterRequest("ada@example.com", "password1", " ", "Lovelace", "123");
        assertThat(violatedFields(request)).contains("firstName");
    }

    @Test
    void registerAcceptsValidRequest() {
        var request = new CustomerDtos.RegisterRequest("ada@example.com", "password1", "Ada", "Lovelace", "123");
        assertThat(violatedFields(request)).isEmpty();
    }

    @Test
    void addressRejectsBlankRequiredFields() {
        var request = new CustomerDtos.AddressRequest("", "apt 2", "", "", "", false);
        assertThat(violatedFields(request))
                .contains("addressLine1", "city", "postalCode", "country")
                .doesNotContain("addressLine2", "isDefault");
    }

    @Test
    void addressAcceptsBlankOptionalSecondLine() {
        var request = new CustomerDtos.AddressRequest("1 Main St", "", "London", "E1 1AA", "UK", true);
        assertThat(violatedFields(request)).isEmpty();
    }
}
