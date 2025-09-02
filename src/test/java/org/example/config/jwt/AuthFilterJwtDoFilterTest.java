package org.example.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.service.JwtService;
import org.springframework.security.core.userdetails.User;
import org.example.web.request.LoginRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthFilterJwtDoFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthFilterJWT jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;
    private ObjectMapper objectMapper;
    private User coreUserDetails;
    private String authHeaderToken;
    private String authHeaderTokenWB;

    @BeforeEach
    void setUp() {

        coreUserDetails = new User("john.doe@example.com", "asdF4cv3vse",
                java.util.Collections.emptyList());
        //authHeaderToken = "Bearer " + this.jwtService.generateToken(testUser);
        //authHeaderTokenWB = this.jwtService.generateToken(testUser);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        objectMapper = new ObjectMapper();
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternalWithValidTokenShouldAuthenticateUser() throws Exception {
        String token = "valid.jwt.token";
        String username = "john.doe@example.com";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(username);
        loginRequest.setPassword("noEncodePass");

        request.addHeader("Authorization", "Bearer " + token);
        request.setMethod("POST");
        request.setContentType("application/json");
        request.setContent(objectMapper.writeValueAsBytes(loginRequest));

        when(jwtService.getUsernameFromToken(token)).thenReturn(username);

        ;
        when(userDetailsService.loadUserByUsername(username)).thenReturn(coreUserDetails);
        when(jwtService.isTokenValid(token, coreUserDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(jwtService).isTokenValid(token, coreUserDetails);
    }

    @Test
    void doFilterInternalWithValidTokenShouldAuthenticateUser2() throws Exception {

        String username = coreUserDetails.getUsername();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(username);
        loginRequest.setPassword(coreUserDetails.getPassword());

        request.addHeader("Authorization", authHeaderToken);
        request.setMethod("POST");
        request.setContentType("application/json");
        request.setContent(objectMapper.writeValueAsBytes(loginRequest));

        doReturn("john.doe@example.com").when(jwtService).getUsernameFromToken(authHeaderTokenWB);

        UserDetails userDetails = coreUserDetails;

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(authHeaderTokenWB, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(jwtService).isTokenValid(authHeaderTokenWB, userDetails);
    }

    @Test
    void doFilterInternalWithValidTokenShouldThrowNotFoundExceptionBadOperation() throws Exception {
        String token = "valid.jwt.token";
        String username = coreUserDetails.getUsername();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(username);
        loginRequest.setPassword(coreUserDetails.getPassword());

        request.addHeader("Authorization", token);
        request.setMethod("POST");
        request.setContentType("application/json");
        request.setContent(objectMapper.writeValueAsBytes(loginRequest));

        when(jwtService.getUsernameFromToken(token)).thenReturn(username);

        UserDetails userDetails = coreUserDetails;
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        //jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        AuthenticationCredentialsNotFoundException e = assertThrows(AuthenticationCredentialsNotFoundException.class,
                () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
        assertEquals(" Bad Operation ", e.getMessage());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        //verify(jwtService, never()).getUsernameFromToken(anyString());
    }

    @Test
    void doFilterInternalNoAuthorizationHeaderShouldProceedWithoutAuthentication() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("noEncodePass");

        request.setMethod("POST");
        request.setContentType("application/json");
        request.setContent(objectMapper.writeValueAsBytes(loginRequest));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).getUsernameFromToken(anyString());
    }

    @Test
    void doFilterInternalTokenInvalidShouldThrowJwtException() throws Exception {
        String token = "malformed.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.getUsernameFromToken(token)).thenThrow(new IllegalArgumentException("Invalid token"));

        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
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
                .thenThrow(new UsernameNotFoundException("UserCustom not found"));

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class, () -> {
                    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
                });

        assertEquals("UserCustom not found", exception.getMessage());
        verify(userDetailsService).loadUserByUsername(username);
    }
}
