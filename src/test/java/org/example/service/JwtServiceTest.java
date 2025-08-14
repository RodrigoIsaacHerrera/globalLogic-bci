package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.security.SignatureException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.example.data.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {JwtService.class})
@ExtendWith(SpringExtension.class)
class JwtServiceTest {
    @Autowired
    private JwtService jwtService;

    /**
     * Test {@link JwtService#generateToken(User)}.
     *
     * <ul>
     *   <li>When {@link User} {@link User#getId()} return randomUUID.
     *   <li>Then calls {@link User#getEmail()}.
     * </ul>
     *
     * <p>Method under test: {@link JwtService#generateToken(User)}
     */
    @Test
    @DisplayName(
            "Test generateToken(User); when User getId() return randomUUID; then calls getEmail()")
    void testGenerateToken_whenUserGetIdReturnRandomUUID_thenCallsGetEmail() {
        // Arrange
        User user = mock(User.class);
        when(user.getEmail()).thenThrow(new SignatureException("An error occurred"));
        when(user.getId()).thenReturn(UUID.randomUUID());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<UUID>any());
        doNothing().when(user).setName(Mockito.<String>any());
        doNothing().when(user).setPassword(Mockito.<String>any());
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");

        // Act
        jwtService.generateToken(user);

        // Assert
        verify(user).getEmail();
        verify(user, atLeast(1)).getId();
        verify(user).setEmail("jane.doe@example.org");
        verify(user).setId(isA(UUID.class));
        verify(user).setName("Name");
        verify(user).setPassword("iloveyou");
    }

    /**
     * Test {@link JwtService#getToken(Map, User)}.
     *
     * <ul>
     *   <li>When {@link User#User()} Email is {@code jane.doe@example.org}.
     *   <li>Then {@link HashMap#HashMap()} size is four.
     * </ul>
     *
     * <p>Method under test: {@link JwtService#getToken(Map, User)}
     */
    @Test
    @DisplayName(
            "Test getToken(Map, User); when User() Email is 'jane.doe@example.org'; then HashMap() size is four")
    void testGetToken_whenUserEmailIsJaneDoeExampleOrg_thenHashMapSizeIsFour() {
        // Arrange
        HashMap<String, Object> exClaims = new HashMap<>();

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");

        // Act
        jwtService.getToken(exClaims, user);

        // Assert
        assertEquals(4, exClaims.size());
        assertTrue(exClaims.containsKey("id"));
        assertTrue((Boolean) exClaims.get("isActive"));
    }

}
