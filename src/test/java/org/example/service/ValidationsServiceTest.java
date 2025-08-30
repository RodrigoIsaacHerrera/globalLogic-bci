package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {ValidationsService.class})
@ExtendWith(SpringExtension.class)
class ValidationsServiceTest {
    @Autowired
    private ValidationsService validationsService;

    /**
     * Test {@link ValidationsService#validationParams(String, String)}.
     *
     * <ul>
     *   <li>Then return {@code false VALID EMAIL *** INVALID PASSWORD ***}.
     * </ul>
     *
     * <p>Method under test: {@link ValidationsService#validationParams(String, String)}
     */
    @Test
    @DisplayName(
            "Test testValidationParams_thenReturnFalseValidEmailInvalidPassword(String, String); then return 'false VALID EMAIL *** INVALID PASSWORD ***'")
    void testValidationParams_thenReturnFalseValidEmailInvalidPassword() {
        // Arrange,
        // Act and Assert
        assertEquals(
                "false VALID EMAIL ***  INVALID PASSWORD *** ",
                validationsService.validationParams("jane.doe@example.org", "Valid Pss"));
    }

    /**
     * Test {@link ValidationsService#validationParams(String, String)}.
     *
     * <ul>
     *   <li>Then return {@code false INVALID EMAIL *** INVALID PASSWORD ***}.
     * </ul>
     *
     * <p>Method under test: {@link ValidationsService#validationParams(String, String)}
     */
    @Test
    @DisplayName(
            "Test testValidationParams_thenReturnFalseInvalidEmailInvalidPassword(String, String); " +
                    "then return 'false INVALID EMAIL *** INVALID PASSWORD ***'")
    void testValidationParams_thenReturnFalseInvalidEmailInvalidPassword() {
        // Arrange,
        // Act and Assert
        assertEquals(
                "false INVALID EMAIL ***  INVALID PASSWORD *** ",
                validationsService.validationParams("jane.doeexample.org", "Valid Pss"));
    }

    /**
     * Test {@link ValidationsService#validationParams(String, String)}.
     *
     * <ul>
     *   <li>Then return {@code false INVALID EMAIL *** VALID PASSWORD ***}.
     * </ul>
     *
     * <p>Method under test: {@link ValidationsService#validationParams(String, String)}
     */
    @Test
    @DisplayName(
            "Test testValidationParams_thenReturnFalseInvalidEmailValidPassword(String, String);" +
                    " then return 'false INVALID EMAIL *** VALID PASSWORD ***'")
    void testValidationParams_thenReturnFalseInvalidEmailValidPassword() {
        // Arrange,
        // Act and Assert
        assertEquals(
                "false INVALID EMAIL ***  VALID PASSWORD *** ",
                validationsService.validationParams("jane.doeexample.org", "Va4lid3ss"));
    }

    /**
     * Test {@link ValidationsService#validationParams(String, String)}.
     *
     * <ul>
     *   <li>Then return {@code true VALID EMAIL *** VALID PASSWORD ***}.
     * </ul>
     *
     * <p>Method under test: {@link ValidationsService#validationParams(String, String)}
     */
    @Test
    @DisplayName(
            "Test testValidationParams_thenReturnFalseValidEmailValidPassword(String, String); " +
                    "then return 'true VALID EMAIL *** VALID PASSWORD ***'")
    void testValidationParams_thenReturnFalseValidEmailValidPassword() {
        // Arrange,
        // Act and Assert
        assertEquals(
                "true VALID EMAIL ***  VALID PASSWORD *** ",
                validationsService.validationParams("jane.doe@example.org", "Va4lid3ss"));
    }

    /**
     * Test {@link ValidationsService#validationParams(String, String)}.
     *
     * <ul>
     *   <li>Then return {@code true VALID EMAIL *** VALID PASSWORD ***}.
     * </ul>
     *
     * <p>Method under test: {@link ValidationsService#validationParams(String, String)}
     */
    @Test
    @DisplayName(
            "Test testValidationParams_thenReturnFalseWithSpecialCharactersParams(String, String); " +
                    "then return 'false INVALID EMAIL ***  INVALID PASSWORD ***'")
    void testValidationParams_thenReturnFalseWithSpecialCharactersParams() {
        // Arrange,
        // Act and Assert
        assertEquals(
                "false INVALID EMAIL ***  INVALID PASSWORD *** ",
                validationsService.validationParams("$%^&*^%", "+_*_)(&^%^%$$##@"));
    }
}