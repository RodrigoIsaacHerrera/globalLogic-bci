package org.example.service;

import org.example.data.entity.Phone;
import org.example.data.entity.UserCustom;
import org.example.data.mappers.PhoneMapper;
import org.example.data.repository.PhonesRepository;
import org.example.data.repository.UsersRepository;
import org.example.web.reponse.LoginResponse;
import org.example.web.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthServiceIntegrationLoginTest {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PhonesRepository phonesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @SpyBean
    private AuthService spyAuthService;

    private UserCustom testUserCustom;

    private String authHeader;
    private String authHeaderWB;

    @BeforeEach
    void setUp() {
        // Limpiar datos previos
        phonesRepository.deleteAll();
        usersRepository.deleteAll();

        // Crear y guardar usuario de prueba
        testUserCustom = new UserCustom();
        testUserCustom.setId(UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"));
        testUserCustom.setName("John Doe");
        testUserCustom.setEmail("john.doe@example.com");
        testUserCustom.setPassword(passwordEncoder.encode("asdF4cv3vse"));

        testUserCustom = usersRepository.save(testUserCustom);

        authHeader = "Bearer " + this.jwtService.generateToken(testUserCustom);
        authHeaderWB = this.jwtService.generateToken(testUserCustom);


        // Guardar teléfono asociado
        Phone phone = new Phone();
        phone.setNumber(1234567L);
        phone.setCitycode(1);
        phone.setCountrycode("57");
        phone.setUserId(testUserCustom.getId());

        phonesRepository.save(phone);
    }

    /**
     * Caso 1: Credenciales válidas y token válido → Login exitoso
     */
    @Test
    void login_ValidCredentialsAndToken_ReturnsLoginResponse() {
        setUp();
        // Simulamos que el token es válido
        doReturn(true).when(spyAuthService).verificationToken("john.doe@example.com", "Bearer valid-token");

        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "asdF4cv3vse");

        // Primero: verificamos que AuthenticationManager funcione (opcional, pero útil)
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals("john.doe@example.com", auth.getName());


        LoginResponse result = spyAuthService.login(loginRequest, authHeader);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("John Doe", result.getName());
        assertTrue(result.getToken().contains("eyJhbGciOiJIUzI1NiJ9."));
        assertTrue(result.isActive());
        assertNotNull(result.getCreated());
        assertNotNull(result.getLastLogin());
        assertEquals(1, result.getPhones().size());

        PhoneMapper phone = result.getPhones().get(0);
        assertEquals(1234567L, phone.getNumber());
        assertEquals(1, phone.getCitycode());
        assertEquals("57", phone.getCountrycode());
    }
    /**
     * Caso 2: Credenciales válidas y token sin "bearer" válido → Login exitoso
     */
    @Test
    void login_ValidCredentialsAndTokenWithoutBearer_ReturnsLoginResponse() {
        setUp();
        // Simulamos que el token es válido
        doReturn(true).when(spyAuthService).verificationToken("john.doe@example.com", "Bearer valid-token");

        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "asdF4cv3vse");

        // Primero: verificamos que AuthenticationManager funcione (opcional, pero útil)
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals("john.doe@example.com", auth.getName());


        LoginResponse result = spyAuthService.login(loginRequest, authHeaderWB);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("John Doe", result.getName());
        assertTrue(result.getToken().contains("eyJhbGciOiJIUzI1NiJ9."));
        assertTrue(result.isActive());
        assertNotNull(result.getCreated());
        assertNotNull(result.getLastLogin());
        assertEquals(1, result.getPhones().size());

        PhoneMapper phone = result.getPhones().get(0);
        assertEquals(1234567L, phone.getNumber());
        assertEquals(1, phone.getCitycode());
        assertEquals("57", phone.getCountrycode());
    }
}
