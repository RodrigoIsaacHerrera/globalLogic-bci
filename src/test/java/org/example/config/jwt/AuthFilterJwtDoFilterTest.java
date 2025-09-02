package org.example.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.data.entity.UserCustom;
import org.example.service.JwtService;
import org.example.web.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.FilterChain;

import java.security.Key;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthFilterJwtDoFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @SpyBean
    @InjectMocks
    private AuthFilterJWT authFilterJWT;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;
    private ObjectMapper objectMapper;
    private UserCustom testUserCustom;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        objectMapper = new ObjectMapper();

        testUserCustom = new UserCustom();
        testUserCustom.setId(UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"));
        testUserCustom.setName("John Doe");
        testUserCustom.setEmail("john.doe@example.com");
        testUserCustom.setPassword("asdF4cv3vse");

        this.jwtService.generateToken(testUserCustom);

        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternalWithValidTokenShouldAuthenticateUser() throws Exception {
        setUp();
        Map<String, Object> claims = new HashMap<>();
        String token = getTokenMock(claims, testUserCustom);
        String username = "john.doe@example.com";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(username);
        loginRequest.setPassword("asdF4cv3vse");

        request.addHeader("Authorization", "Bearer " + token);
        request.setMethod("POST");
        request.setContentType("application/json");
        request.setContent(objectMapper.writeValueAsBytes(loginRequest));

        UserDetails userDetails = new User(username, "asdF4cv3vse", java.util.Collections.emptyList());
        doReturn("john.doe@example.com").when(jwtService).getUsernameFromToken(token);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        authFilterJWT.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(jwtService).isTokenValid(token, userDetails);
    }

    @Test
    void doFilterInternalNoAuthorizationHeaderShouldProceedWithoutAuthentication() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("noEncodePass");

        request.setMethod("POST");
        request.setContentType("application/json");
        request.setContent(objectMapper.writeValueAsBytes(loginRequest));

        authFilterJWT.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).getUsernameFromToken(anyString());
    }

    @Test
    void doFilterInternalTokenInvalidShouldThrowJwtException() throws Exception {
        String token = "malformed.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.getUsernameFromToken(token)).thenThrow(new IllegalArgumentException("Invalid token"));

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            authFilterJWT.doFilterInternal(request, response, filterChain);
        });
    }

    @Test
    void doFilterInternalValidTokenButUserNotFoundShouldThrowUsernameNotFoundException() throws Exception {
        String token = "valid.token";
        String username = "unknownuser@example.com";

        request.addHeader("Authorization", "Bearer " + token);
        request.setMethod("POST");
        request.setContentType("application/json");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("someone@example.com");
        loginRequest.setPassword("noEncodePass");

        request.setContent(new ObjectMapper().writeValueAsBytes(loginRequest));

        when(jwtService.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username))
                .thenThrow(new UsernameNotFoundException("User not found"));

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> {
                    authFilterJWT.doFilterInternal(request, response, filterChain);
                });

        assertEquals("User not found", exception.getMessage());
        verify(userDetailsService).loadUserByUsername(username);
    }

    static protected String getTokenMock(Map<String, Object> exClaims, UserCustom userCustom) {

        String dateOrigin = new Date(System.currentTimeMillis()).toString();

        exClaims.put("isActive", true);
        exClaims.put("lastLogin", dateOrigin);
        exClaims.put("created", dateOrigin);
        exClaims.put("Id", userCustom.getId().toString());

        return Jwts.builder()
                .setClaims(exClaims)
                .setSubject(userCustom.getEmail())
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith(getKeyMock(),SignatureAlgorithm.HS256).compact();
    }
    static protected Key getKeyMock() {
        byte[] keyBytes = Decoders.BASE64.decode("586E3272357578982F413F4428472B4B6250655368566B598071733676397924");
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
