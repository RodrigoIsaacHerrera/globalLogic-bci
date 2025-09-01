package org.example.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.MalformedJwtException;
import org.example.shared.ErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ContextConfiguration(classes = {GlobalExceptionHandler.class})
@ExtendWith(SpringExtension.class)
class GlobalExceptionHandlerTest {
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    /**
     * Test {@link GlobalExceptionHandler#handleNoHandlerFound(NoHandlerFoundException)}.
     *
     * <p>Method under test: {@link
     * GlobalExceptionHandler#handleNoHandlerFound(NoHandlerFoundException)}
     */
    @Test
    @DisplayName("Test handleNoHandlerFound(NoHandlerFoundException)")

    void testHandleNoHandlerFound() {
        // Arrange and Act
        ResponseEntity<ErrorResponse> actualHandleNoHandlerFoundResult =
                globalExceptionHandler.handleNoHandlerFound(
                        new NoHandlerFoundException(
                                "https://example.org/example", "https://example.org/example", new HttpHeaders()));

        // Assert
        ErrorResponse body = actualHandleNoHandlerFoundResult.getBody();
        Assertions.assertNotNull(body);
        assertEquals("The requested resource: NOT_FOUND", body.getDetail());
        assertEquals(404, body.getCode());
        assertEquals(404, actualHandleNoHandlerFoundResult.getStatusCodeValue());
        assertEquals(HttpStatus.NOT_FOUND, actualHandleNoHandlerFoundResult.getStatusCode());
        assertTrue(actualHandleNoHandlerFoundResult.hasBody());
        assertTrue(actualHandleNoHandlerFoundResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link GlobalExceptionHandler#handleGenericException(Exception)}.
     *
     * <p>Method under test: {@link GlobalExceptionHandler#handleGenericException(Exception)}
     */
    @Test
    @DisplayName("Test handleGenericException(Exception)")

    void testHandleGenericException() {
        // Arrange and Act
        ResponseEntity<ErrorResponse> actualHandleGenericExceptionResult =
                globalExceptionHandler.handleGenericException(new Exception());

        // Assert
        ErrorResponse body = actualHandleGenericExceptionResult.getBody();
        Assertions.assertNotNull(body);
        assertEquals("An unexpected error occurred BAD_REQUEST", body.getDetail());
        assertEquals(400, body.getCode());
        assertEquals(400, actualHandleGenericExceptionResult.getStatusCodeValue());
        assertEquals(
                HttpStatus.BAD_REQUEST, actualHandleGenericExceptionResult.getStatusCode());
        assertTrue(actualHandleGenericExceptionResult.hasBody());
        assertTrue(actualHandleGenericExceptionResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link GlobalExceptionHandler#handleGenericException(Exception)}.
     *
     * <p>Method under test: {@link GlobalExceptionHandler#handleGenericException(Exception)}
     */
    @Test
    @DisplayName("Test testHandleGenericExceptionUserExists(Exception)")

    void testHandleGenericExceptionUserExists() {
        // Arrange and Act
        ResponseEntity<ErrorResponse> actualHandleGenericExceptionResult =
                globalExceptionHandler.handleGenericException(new Exception("SQL"));

        // Assert
        ErrorResponse body = actualHandleGenericExceptionResult.getBody();
        Assertions.assertNotNull(body);
        assertEquals("USER EXISTS - BAD_REQUEST", body.getDetail());
        assertEquals(400, body.getCode());
        assertEquals(400, actualHandleGenericExceptionResult.getStatusCodeValue());
        assertEquals(
                HttpStatus.BAD_REQUEST, actualHandleGenericExceptionResult.getStatusCode());
        assertTrue(actualHandleGenericExceptionResult.hasBody());
        assertTrue(actualHandleGenericExceptionResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link
     * GlobalExceptionHandler#handleMethodNotSupported(HttpRequestMethodNotSupportedException)}.
     *
     * <p>Method under test: {@link
     * GlobalExceptionHandler#handleMethodNotSupported(HttpRequestMethodNotSupportedException)}
     */
    @Test
    @DisplayName("Test handleMethodNotSupported(HttpRequestMethodNotSupportedException)")

    void testHandleMethodNotSupported() {
        // Arrange and Act
        ResponseEntity<ErrorResponse> actualHandleMethodNotSupportedResult =
                globalExceptionHandler.handleMethodNotSupported(
                        new HttpRequestMethodNotSupportedException("https://example.org/example"));

        // Assert
        ErrorResponse body = actualHandleMethodNotSupportedResult.getBody();
        Assertions.assertNotNull(body);
        assertEquals(
                "The HTTP verb is not supported for this endpoint. METHOD_NOT_ALLOWED", body.getDetail());
        assertEquals(405, body.getCode());
        assertEquals(405, actualHandleMethodNotSupportedResult.getStatusCodeValue());
        assertEquals(
                HttpStatus.METHOD_NOT_ALLOWED, actualHandleMethodNotSupportedResult.getStatusCode());
        assertTrue(actualHandleMethodNotSupportedResult.hasBody());
        assertTrue(actualHandleMethodNotSupportedResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link
     * GlobalExceptionHandler#handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException)}.
     *
     * <p>Method under test: {@link
     * GlobalExceptionHandler#handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException)}
     */
    @Test
    @DisplayName("Test handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException)")

    void testHandleMediaTypeNotSupported() {
        // Arrange and Act
        ResponseEntity<ErrorResponse> actualHandleMediaTypeNotSupportedResult =
                globalExceptionHandler.handleMediaTypeNotSupported(
                        new HttpMediaTypeNotSupportedException("https://example.org/example"));

        // Assert
        ErrorResponse body = actualHandleMediaTypeNotSupportedResult.getBody();
        Assertions.assertNotNull(body);
        assertEquals("The media type is not supported. UNSUPPORTED_MEDIA_TYPE", body.getDetail());
        assertEquals(415, body.getCode());
        assertEquals(415, actualHandleMediaTypeNotSupportedResult.getStatusCodeValue());
        assertEquals(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE, actualHandleMediaTypeNotSupportedResult.getStatusCode());
        assertTrue(actualHandleMediaTypeNotSupportedResult.hasBody());
        assertTrue(actualHandleMediaTypeNotSupportedResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link
     * GlobalExceptionHandler#handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException)}.
     *
     * <p>Method under test: {@link
     * GlobalExceptionHandler#handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException)}
     */
    @Test
    @DisplayName("Test handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException)")

    void testHandleMediaTypeNotAcceptable() {
        // Arrange and Act
        ResponseEntity<ErrorResponse> actualHandleMediaTypeNotAcceptableResult =
                globalExceptionHandler.handleMediaTypeNotAcceptable(
                        new HttpMediaTypeNotAcceptableException("https://example.org/example"));

        // Assert
        ErrorResponse body = actualHandleMediaTypeNotAcceptableResult.getBody();
        Assertions.assertNotNull(body);
        assertEquals("The media type is not acceptable. NOT_ACCEPTABLE", body.getDetail());
        assertEquals(406, body.getCode());
        assertEquals(406, actualHandleMediaTypeNotAcceptableResult.getStatusCodeValue());
        assertEquals(
                HttpStatus.NOT_ACCEPTABLE, actualHandleMediaTypeNotAcceptableResult.getStatusCode());
        assertTrue(actualHandleMediaTypeNotAcceptableResult.hasBody());
        assertTrue(actualHandleMediaTypeNotAcceptableResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link
     * GlobalExceptionHandler#handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException)}.
     *
     * <p>Method under test: {@link
     * GlobalExceptionHandler#handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException)}
     */
    @Test
    @DisplayName(
            "Test handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException)")
    void testHandleAuthenticationCredentialsNotFoundException() {
        // Arrange and Act
        ResponseEntity<ErrorResponse> actualHandleAuthenticationCredentialsNotFoundExceptionResult =
                globalExceptionHandler.handleAuthenticationCredentialsNotFoundException(
                        new AuthenticationCredentialsNotFoundException("Msg"));

        // Assert
        ErrorResponse body = actualHandleAuthenticationCredentialsNotFoundExceptionResult.getBody();
        assertEquals("Error authentication.  FORBIDDEN", body.getDetail());
        assertEquals(403, body.getCode());
        assertEquals(
                403, actualHandleAuthenticationCredentialsNotFoundExceptionResult.getStatusCodeValue());
        assertEquals(
                HttpStatus.FORBIDDEN,
                actualHandleAuthenticationCredentialsNotFoundExceptionResult.getStatusCode());
        assertTrue(actualHandleAuthenticationCredentialsNotFoundExceptionResult.hasBody());
        assertTrue(actualHandleAuthenticationCredentialsNotFoundExceptionResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link GlobalExceptionHandler#handleUsernameNotFoundException(UsernameNotFoundException)}.
     *
     * <p>Method under test: {@link
     * GlobalExceptionHandler#handleUsernameNotFoundException(UsernameNotFoundException)}
     */
    @Test
    @DisplayName("Test handleUsernameNotFoundException(UsernameNotFoundException)")

    void testHandleUsernameNotFoundException() {
        // Arrange and Act
        ResponseEntity<ErrorResponse> actualHandleUsernameNotFoundExceptionResult =
                globalExceptionHandler.handleUsernameNotFoundException(
                        new UsernameNotFoundException("Msg"));

        // Assert
        ErrorResponse body = actualHandleUsernameNotFoundExceptionResult.getBody();
        assertEquals("Error authentication . Msg", body.getDetail());
        assertEquals(404, body.getCode());
        assertEquals(404, actualHandleUsernameNotFoundExceptionResult.getStatusCodeValue());
        assertEquals(HttpStatus.NOT_FOUND, actualHandleUsernameNotFoundExceptionResult.getStatusCode());
        assertTrue(actualHandleUsernameNotFoundExceptionResult.hasBody());
        assertTrue(actualHandleUsernameNotFoundExceptionResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link GlobalExceptionHandler#handleMalformedJwt(MalformedJwtException)}.
     *
     * <p>Method under test: {@link GlobalExceptionHandler#handleMalformedJwt(MalformedJwtException)}
     */
    @Test
    @DisplayName("Test handleMalformedJwt(MalformedJwtException)")

    void testHandleMalformedJwt() {
        // Arrange and Act
        ResponseEntity<ErrorResponse> actualHandleMalformedJwtResult =
                globalExceptionHandler.handleMalformedJwt(new MalformedJwtException("An error occurred"));

        // Assert
        ErrorResponse body = actualHandleMalformedJwtResult.getBody();
        assertEquals("Error authentication MALFORMED_TOKEN. UNAUTHORIZED", body.getDetail());
        assertEquals(401, body.getCode());
        assertEquals(401, actualHandleMalformedJwtResult.getStatusCodeValue());
        assertEquals(HttpStatus.UNAUTHORIZED, actualHandleMalformedJwtResult.getStatusCode());
        assertTrue(actualHandleMalformedJwtResult.hasBody());
        assertTrue(actualHandleMalformedJwtResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link GlobalExceptionHandler#handleDuplicateKeyException(DuplicateKeyException)}.
     *
     * <p>Method under test: {@link GlobalExceptionHandler#handleDuplicateKeyException(DuplicateKeyException)}
     */
    @Test
    @DisplayName("Test handleDuplicateKeyException(DuplicateKeyException)")

    void testHandlehandleDuplicateKeyException() {
        // Arrange and Act
        ResponseEntity<ErrorResponse> actualHandleDuplicateKeyException =
                globalExceptionHandler.handleDuplicateKeyException(new DuplicateKeyException(" Operation Fail. DB reject operation"));

        // Assert
        ErrorResponse body = actualHandleDuplicateKeyException.getBody();
        assertEquals("An unexpected DB error occurred  NOT_ACCEPTABLE Operation Fail. DB reject operation", body.getDetail());
        assertEquals(406, body.getCode());
        assertEquals(406, actualHandleDuplicateKeyException.getStatusCodeValue());
        assertEquals(HttpStatus.NOT_ACCEPTABLE, actualHandleDuplicateKeyException.getStatusCode());
        assertTrue(actualHandleDuplicateKeyException.hasBody());
        assertTrue(actualHandleDuplicateKeyException.getHeaders().isEmpty());
    }

}
