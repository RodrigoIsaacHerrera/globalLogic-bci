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
            "Test validationParams(String, String); then return 'false VALID EMAIL *** INVALID PASSWORD ***'")
    void testValidationParams_thenReturnFalseValidEmailInvalidPassword() {
        // Arrange, Act and Assert
        assertEquals(
                "false VALID EMAIL ***  INVALID PASSWORD *** ",
                validationsService.validationParams("jane.doe@example.org", "Valid Pss"));
    }
}
