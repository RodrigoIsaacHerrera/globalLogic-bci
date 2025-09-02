package org.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.data.entity.UserCustom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

@ContextConfiguration(classes = { JwtService.class })
@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    /**
     * Test {@link JwtService#getToken(Map, UserCustom)}.
     *
     * <ul>
     *   <li>When {@link UserCustom#UserCustom()} Email is {@code jane.doe@example.org}.
     *   <li>Then {@link HashMap#HashMap()} size is four.
     * </ul>
     *
     * <p>Method under test: {@link JwtService#getToken(Map, UserCustom)}
     */
    @Test
    void testGetToken_whenUserEmailIsJaneDoeExampleOrg_thenHashMapSizeIsFour() {
        // Arrange
        HashMap<String, Object> exClaims = new HashMap<>();

        UserCustom userCustom = new UserCustom();
        userCustom.setEmail("jane.doe@example.org");
        userCustom.setId(UUID.randomUUID());
        userCustom.setName("Jane Doe");
        userCustom.setPassword("iloveyou");

        // Act
        jwtService.getToken(exClaims, userCustom);

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
        UserCustom userCustom = new UserCustom(UUID.fromString("ef199728-21aa-4a3c-a846-66202c1866c1"), "abc", "abc", "abc");
        String expected = "abc";
        String token = jwtService.getToken(exClaims, userCustom);
        String actual = jwtService.getUsernameFromToken(token);

        assertEquals(expected, actual);
    }

    @Test
    void getIdFromToken() {
        Map<String, Object> exClaims = new HashMap<>();
        UserCustom userCustom = new UserCustom(UUID.fromString("ef199728-21aa-4a3c-a846-66202c1866c1"), "abc", "abc", "abc");
        String expected = "ef199728-21aa-4a3c-a846-66202c1866c1";
        String token = jwtService.getToken(exClaims, userCustom);
        String actual = jwtService.getIdFromToken(token);

        assertEquals(expected, actual);
    }

    @Test
    void getIdFromTokenThrowsNullPointerException() {
        Map<String, Object> exClaims = new HashMap<>();
        UserCustom userCustom = new UserCustom();

        assertThrows(NullPointerException.class, () -> jwtService.getToken(exClaims, userCustom));
    }

    @Test
    public void isTokenValidThrowsMalformedJwtException() {
        String token = "abc";
        UserDetails userDetails = null;

        assertThrows(MalformedJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void isTokenValidReturnTrue() {
        Map<String, Object> exClaims = new HashMap<>();
        UserCustom userCustom = new UserCustom(UUID.fromString("ef199728-21aa-4a3c-a846-66202c1866c1"), "Jane Doe",
                "jane.doe@example.org", "abc1tG4fd");
        String token = jwtService.getToken(exClaims, userCustom);
        boolean actual = jwtService.isTokenValid(token, userCustom);

        assertTrue(actual);
    }

    @Test
    void isTokenValidThrowsJwtExceptionWithMessageExpiredToken() {
        Map<String, Object> exClaims = new HashMap<>();
        UserCustom userCustom = new UserCustom(UUID.fromString("ef199728-21aa-4a3c-a846-66202c1866c1"), "Jane Doe",
                "jane.doe@example.org", "abc1tG4fd");
        String token = getTokenExpired(exClaims, userCustom);
        String expected = "Expired Token";

        JwtException exception = assertThrows(JwtException.class, () -> jwtService.isTokenValid(token, userCustom));
        assertEquals(expected, exception.getMessage());
    }

    static private String getTokenExpired(Map<String, Object> exClaims, UserCustom userCustom) {

        String fechaAtrasada = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 7)).toString();
        Date fecha7HorasAntes = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 7));

        exClaims.put("isActive", true);
        exClaims.put("lastLogin", fechaAtrasada);
        exClaims.put("created", fechaAtrasada);
        exClaims.put("Id", userCustom.getId().toString());

        return Jwts.builder()
                .setClaims(exClaims)
                .setSubject(userCustom.getEmail())
                .setExpiration(fecha7HorasAntes)
                .signWith(getKeyExpired(), SignatureAlgorithm.HS256).compact();
    }
    static protected Key getKeyExpired() {
        byte[] keyBytes = Decoders.BASE64.decode("586E3272357578982F413F4428472B4B6250655368566B598071733676397924");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    public void generateTokenReturnGetToken() {
        UserCustom userCustom = new UserCustom(UUID.fromString("ef199728-21aa-4a3c-a846-66202c1866c1"), "Jane Doe",
                "jane.doe@example.org", "abc1tG4fd");
        String expected = "eyJhbGciOiJIUzI1NiJ9.";
        String actual = jwtService.generateToken(userCustom);
        boolean contains = actual.contains(expected);

        assertTrue(contains);

    }
}
