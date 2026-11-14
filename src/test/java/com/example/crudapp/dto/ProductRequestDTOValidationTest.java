package com.example.crudapp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Tests Jakarta Bean Validation without launching Spring or Oracle. */
class ProductRequestDTOValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void validRequest_hasNoConstraintViolations() {
        assertTrue(validator.validate(validRequest()).isEmpty());
    }

    @Test
    void blankName_isRejected() {
        ProductRequestDTO dto = validRequest();
        dto.setName("   ");
        assertViolationOn(dto, "name");
    }

    @Test
    void tooLongName_isRejected() {
        ProductRequestDTO dto = validRequest();
        dto.setName("A".repeat(151));
        assertViolationOn(dto, "name");
    }

    @Test
    void tooLongDescription_isRejected() {
        ProductRequestDTO dto = validRequest();
        dto.setDescription("B".repeat(1001));
        assertViolationOn(dto, "description");
    }

    @Test
    void nullPrice_isRejected() {
        ProductRequestDTO dto = validRequest();
        dto.setPrice(null);
        assertViolationOn(dto, "price");
    }

    @Test
    void negativePrice_isRejected() {
        ProductRequestDTO dto = validRequest();
        dto.setPrice(new BigDecimal("-0.01"));
        assertViolationOn(dto, "price");
    }

    @Test
    void negativeQuantity_isRejected() {
        ProductRequestDTO dto = validRequest();
        dto.setQuantity(-1);
        assertViolationOn(dto, "quantity");
    }

    private static ProductRequestDTO validRequest() {
        return ProductRequestDTO.builder()
                .name("Monitor")
                .description("27-inch display")
                .price(new BigDecimal("750.00"))
                .quantity(4)
                .build();
    }

    private static void assertViolationOn(ProductRequestDTO dto, String field) {
        Set<ConstraintViolation<ProductRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> field.equals(v.getPropertyPath().toString())),
                "Expected validation failure for field: " + field);
    }
}
