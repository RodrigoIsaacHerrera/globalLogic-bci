package org.example.config.jwt;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.example.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

@ContextConfiguration(classes = {AuthFilterJWT.class})
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
class AuthFilterJWTTest {
    @Autowired
    private AuthFilterJWT authFilterJWT;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    /**
     * Test {@link AuthFilterJWT#doFilterInternal(HttpServletRequest, HttpServletResponse,
     * FilterChain)}.
     *
     * <p>Method under test: {@link AuthFilterJWT#doFilterInternal(HttpServletRequest,
     * HttpServletResponse, FilterChain)}
     */
    @Test
    @DisplayName("Test doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)")
    void testDoFilterInternal() throws IOException, ServletException {
        // Arrange
        when(jwtService.getUsernameFromToken(Mockito.<String>any()))
                .thenThrow(new AuthenticationCredentialsNotFoundException("Authorization"));
        DefaultMultipartHttpServletRequest request = mock(DefaultMultipartHttpServletRequest.class);
        when(request.getHeader(Mockito.<String>any())).thenReturn("Bearer ");

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> authFilterJWT.doFilterInternal(request, new Response(), mock(FilterChain.class)));
        verify(request).getHeader("Authorization");
        verify(jwtService).getUsernameFromToken("");
    }

    /**
     * Test {@link AuthFilterJWT#doFilterInternal(HttpServletRequest, HttpServletResponse,
     * FilterChain)}.
     *
     * <p>Method under test: {@link AuthFilterJWT#doFilterInternal(HttpServletRequest,
     * HttpServletResponse, FilterChain)}
     */
    @Test
    @DisplayName("Test doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)")
    void testDoFilterInternal2() throws IOException, ServletException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doThrow(new AuthenticationCredentialsNotFoundException("Authorization"))
                .when(filterChain)
                .doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());

        // Act and Assert
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> authFilterJWT.doFilterInternal(request, response, filterChain));
        verify(filterChain).doFilter(isA(ServletRequest.class), isA(ServletResponse.class));
    }

    /**
     * Test {@link AuthFilterJWT#doFilterInternal(HttpServletRequest, HttpServletResponse,
     * FilterChain)}.
     *
     * <ul>
     *   <li>Given {@code https://example.org/example}.
     * </ul>
     *
     * <p>Method under test: {@link AuthFilterJWT#doFilterInternal(HttpServletRequest,
     * HttpServletResponse, FilterChain)}
     */
    @Test
    @DisplayName(
            "Test doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain); given 'https://example.org/example'")
    void testDoFilterInternal_givenHttpsExampleOrgExample() throws IOException, ServletException {
        // Arrange
        DefaultMultipartHttpServletRequest request = mock(DefaultMultipartHttpServletRequest.class);
        when(request.getHeader(Mockito.<String>any())).thenReturn("https://example.org/example");
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doNothing()
                .when(filterChain)
                .doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());

        // Act
        authFilterJWT.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(isA(ServletRequest.class), isA(ServletResponse.class));
        verify(request).getHeader("Authorization");
    }

    /**
     * Test {@link AuthFilterJWT#doFilterInternal(HttpServletRequest, HttpServletResponse,
     * FilterChain)}.
     *
     * <ul>
     *   <li>Given {@link JwtService} {@link JwtService#getUsernameFromToken(String)} return {@code
     *       null}.
     * </ul>
     *
     * <p>Method under test: {@link AuthFilterJWT#doFilterInternal(HttpServletRequest,
     * HttpServletResponse, FilterChain)}
     */
    @Test
    @DisplayName(
            "Test doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain); given JwtService getUsernameFromToken(String) return 'null'")
    void testDoFilterInternal_givenJwtServiceGetUsernameFromTokenReturnNull()
            throws IOException, ServletException {
        // Arrange
        when(jwtService.getUsernameFromToken(Mockito.<String>any())).thenReturn(null);
        DefaultMultipartHttpServletRequest request = mock(DefaultMultipartHttpServletRequest.class);
        when(request.getHeader(Mockito.<String>any())).thenReturn("Bearer ");
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doNothing()
                .when(filterChain)
                .doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());

        // Act
        authFilterJWT.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(isA(ServletRequest.class), isA(ServletResponse.class));
        verify(request).getHeader("Authorization");
        verify(jwtService).getUsernameFromToken("");
    }

    /**
     * Test {@link AuthFilterJWT#doFilterInternal(HttpServletRequest, HttpServletResponse,
     * FilterChain)}.
     *
     * <ul>
     *   <li>Given {@link JwtService} {@link JwtService#getUsernameFromToken(String)} throw {@link
     *       RuntimeException#RuntimeException()}.
     * </ul>
     *
     * <p>Method under test: {@link AuthFilterJWT#doFilterInternal(HttpServletRequest,
     * HttpServletResponse, FilterChain)}
     */
    @Test
    @DisplayName(
            "Test doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain); given JwtService getUsernameFromToken(String) throw RuntimeException()")
    void testDoFilterInternal_givenJwtServiceGetUsernameFromTokenThrowRuntimeException()
            throws IOException, ServletException {
        // Arrange
        when(jwtService.getUsernameFromToken(Mockito.<String>any())).thenThrow(new RuntimeException());
        DefaultMultipartHttpServletRequest request = mock(DefaultMultipartHttpServletRequest.class);
        when(request.getHeader(Mockito.<String>any())).thenReturn("Bearer ");

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> authFilterJWT.doFilterInternal(request, new Response(), mock(FilterChain.class)));
        verify(request).getHeader("Authorization");
        verify(jwtService).getUsernameFromToken("");
    }

    /**
     * Test {@link AuthFilterJWT#doFilterInternal(HttpServletRequest, HttpServletResponse,
     * FilterChain)}.
     *
     * <ul>
     *   <li>Then calls {@link JwtService#isTokenValid(String, UserDetails)}.
     * </ul>
     *
     * <p>Method under test: {@link AuthFilterJWT#doFilterInternal(HttpServletRequest,
     * HttpServletResponse, FilterChain)}
     */
    @Test
    @DisplayName(
            "Test doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain); then calls isTokenValid(String, UserDetails)")
    void testDoFilterInternal_thenCallsIsTokenValid() throws IOException, ServletException {
        // Arrange
        when(jwtService.isTokenValid(Mockito.<String>any(), Mockito.<UserDetails>any()))
                .thenThrow(new AuthenticationCredentialsNotFoundException("Authorization"));
        when(jwtService.getUsernameFromToken(Mockito.<String>any())).thenReturn("janedoe");
        DefaultMultipartHttpServletRequest request = mock(DefaultMultipartHttpServletRequest.class);
        when(request.getHeader(Mockito.<String>any())).thenReturn("Bearer ");

        // Act and Assert
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> authFilterJWT.doFilterInternal(request, new Response(), mock(FilterChain.class)));
        verify(request).getHeader("Authorization");
        verify(jwtService).getUsernameFromToken("");
        verify(jwtService).isTokenValid(eq(""), isNull());
    }

    /**
     * Test {@link AuthFilterJWT#doFilterInternal(HttpServletRequest, HttpServletResponse,
     * FilterChain)}.
     *
     * <ul>
     *   <li>When {@link MockHttpServletRequest#MockHttpServletRequest()}.
     *   <li>Then calls {@link FilterChain#doFilter(ServletRequest, ServletResponse)}.
     * </ul>
     *
     * <p>Method under test: {@link AuthFilterJWT#doFilterInternal(HttpServletRequest,
     * HttpServletResponse, FilterChain)}
     */
    @Test
    @DisplayName(
            "Test doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain); when MockHttpServletRequest(); then calls doFilter(ServletRequest, ServletResponse)")
    void testDoFilterInternal_whenMockHttpServletRequest_thenCallsDoFilter()
            throws IOException, ServletException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doNothing()
                .when(filterChain)
                .doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());

        // Act
        authFilterJWT.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(isA(ServletRequest.class), isA(ServletResponse.class));
    }
}
