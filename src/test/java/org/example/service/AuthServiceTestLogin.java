package org.example.service;

import org.example.data.entity.Phone;
import org.example.data.entity.User;
import org.example.data.repository.PhonesRepository;
import org.example.data.repository.UsersRepository;
import org.example.shared.enums.RolePermissions;
import org.example.web.reponse.LoginResponse;
import org.example.web.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {AuthService.class})
@ExtendWith(SpringExtension.class)
public class AuthServiceTestLogin {

    @Spy
    @InjectMocks
    private AuthService authService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private PhonesRepository phonesRepository;

    @MockBean
    private UsersRepository usersRepository;

    @Mock
    private UserDetailsService userDetailsService;


    private UUID uuidMock;
    private User user;
    private UsernamePasswordAuthenticationToken thingWithUsePass;
    private Optional<User> resultOf;
    private String authHeader;
    private Phone phone;
    private List<Phone> phones;
    private LoginRequest loginRequest;

    @BeforeEach
    public void setup(){
        loginRequest = new LoginRequest("john.doe@example.com", "correctPassword");
        uuidMock = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
        user = new User();
        user.setEmail("john.doe@example.com");
        user.setId(uuidMock);
        user.setName("John Doe");
        user.setPassword("asdF4cv3vse");
        resultOf = Optional.of(user);
        authHeader = "Bearer " + this.jwtService.generateToken(user);

        thingWithUsePass = new UsernamePasswordAuthenticationToken(
                "john.doe@example.com",
                null,
                AuthorityUtils.createAuthorityList(RolePermissions.USER.name()));

        phone = new Phone();
        phone.setId(101L);
        phone.setNumber(1234567L);
        phone.setCitycode(1);
        phone.setCountrycode("57");
        phone.setUserId(uuidMock);
        phones = new ArrayList<>();

        phones.add(phone);
    }

    @Test
    void login_ValidCredentialsAndToken_ReturnsLoginResponse() throws Exception {
        // Arrange
        setup();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "john.doe@example.com",
                null, // La contraseña no se conserva por seguridad
                AuthorityUtils.createAuthorityList(RolePermissions.USER.name()) // o los roles que apliquen
        );
        doReturn(true).when(authService).verificationToken(loginRequest.getEmail(), authHeader);
        when(authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                        loginRequest.getPassword()))).thenReturn(auth);

        when(usersRepository.findByEmailContainingIgnoreCase(loginRequest.getEmail()))
                .thenReturn(resultOf);

        when(phonesRepository.findAllByUserId(user.getId())).thenReturn(Collections.singletonList(phones));

        // Act
        LoginResponse result = authService.login(loginRequest, authHeader);

        // Assert
        assertNotNull(result);
        assertEquals("mock-jwt-token", result.getToken());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(1, result.getPhones().size());
        assertEquals(1234567L, result.getPhones().get(0).getNumber());
        assertTrue(result.isActive());
        //verify(authService).assemblerObjectLogin(any(User.class), anyList());
    }

    @Test
    void login_ValidCredentialsAndToken_ReturnsLoginResponseValidParams() {
        // Arrange
        setup();

        // Simular que el token es válido
        doReturn("69d21041-9a99-4f95-b87f-2128c1197ed2").when(jwtService)
                .getIdFromToken(authHeader.substring(7));
        when(this.authService.verificationToken(loginRequest.getEmail(), authHeader)).thenCallRealMethod();
        // Como verificationToken no está en el snippet, lo mockeamos directamente
        //doReturn(true).when(authService).verificationToken(loginRequest.getEmail(), authHeader);

        // Simular autenticación exitosa
        Authentication authenticatedToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), null, new ArrayList<>()
        );
        doNothing().when(this.authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Simular que el usuario existe
        when(usersRepository.findByEmailContainingIgnoreCase(loginRequest.getEmail()))
                .thenReturn(resultOf);

        // Simular que hay teléfonos
        List<Phone> phones = new ArrayList<>();
        phones.add(phone);
        when(phonesRepository.findAllByUserId(user.getId())).thenReturn(Collections.singletonList(phones));

        // Simular el ensamblador
        LoginResponse expectedResponse = authService.login(loginRequest, authHeader);

        // Assert
        verify(usersRepository).findByEmailContainingIgnoreCase(loginRequest.getEmail());
    }

    @Test
    void testLogin_InvalidToken_ThrowsAuthenticationException() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password123");

        String authHeader = "Bearer invalid-token";

        // Forzar que verificationToken devuelva false
        doReturn(false).when(authService).verificationToken(loginRequest.getEmail(), authHeader);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest, authHeader);
        });

        assertEquals("Bad Token", exception.getMessage());
    }
}
