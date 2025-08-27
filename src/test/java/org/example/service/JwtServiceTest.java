package org.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import io.jsonwebtoken.MalformedJwtException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.example.data.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.*;

@ContextConfiguration(classes = { JwtService.class })
@ExtendWith(SpringExtension.class)
@SpringBootTest
class JwtServiceTest {
    @Autowired
    private JwtService jwtService;

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
    void testGetToken_whenUserEmailIsJaneDoeExampleOrg_thenHashMapSizeIsFour() {
        // Arrange
        HashMap<String, Object> exClaims = new HashMap<>();

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Jane Doe");
        user.setPassword("iloveyou");

        // Act
        jwtService.getToken(exClaims, user);

        // Assert
        assertEquals(4, exClaims.size());
        assertTrue(exClaims.containsKey("Id"));
        assertTrue((Boolean) exClaims.get("isActive"));
    }

    /**
     * Test {@link JwtService#getClaim(String, Function)}.
     *
     * <ul>
     *   <li>When {@code ABC123}.
     *   <li>Then throw {@link MalformedJwtException}.
     * </ul>
     *
     * <p>Method under test: {@link JwtService#getClaim(String, Function)}
     */
    @Test
    void testGetClaim_whenAbc123_thenThrowMalformedJwtException() {
        // Arrange, Act and Assert
        assertThrows(
                MalformedJwtException.class,
                () -> jwtService.getClaim("ABC123", mock(Function.class)));
    }

    @Test
    void getUsernameFromToken() {
        Map<String, Object> exClaims = new HashMap<>();
        User user = new User(UUID.fromString("ef199728-21aa-4a3c-a846-66202c1866c1"), "abc", "abc", "abc");
        String expected = "abc";
        String token = jwtService.getToken(exClaims, user);
        String actual = jwtService.getUsernameFromToken(token);

        assertEquals(expected, actual);
    }

    @Test
    void getIdFromToken() {
        Map<String, Object> exClaims = new HashMap<>();
        User user = new User(UUID.fromString("ef199728-21aa-4a3c-a846-66202c1866c1"), "abc", "abc", "abc");
        String expected = "ef199728-21aa-4a3c-a846-66202c1866c1";
        String token = jwtService.getToken(exClaims, user);
        String actual = jwtService.getIdFromToken(token);

        assertEquals(expected, actual);
    }
}
