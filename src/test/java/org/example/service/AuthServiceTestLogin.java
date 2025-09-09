package org.example.service;

import org.example.data.entity.Phone;
import org.example.data.entity.UserCustom;
import org.example.data.repository.PhonesRepository;
import org.example.data.repository.UsersRepository;
import org.example.shared.enums.RolePermissions;
import org.example.web.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {AuthService.class})
@ExtendWith(SpringExtension.class)
public class AuthServiceTestLogin {

    @Spy
    @InjectMocks
    private AuthService authService;

    @MockBean
    private ValidationsService validationsService;

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
    private UserCustom userCustom;
    private UsernamePasswordAuthenticationToken thingWithUsePass;
    private Optional<UserCustom> resultOf;
    private String authHeader;
    private Phone phone;
    private List<Phone> phones;
    private LoginRequest loginRequest;

    @BeforeEach
    public void setup(){
        loginRequest = new LoginRequest("john.doe@example.com", "correctPassword");
        uuidMock = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
        userCustom = new UserCustom();
        userCustom.setEmail("john.doe@example.com");
        userCustom.setId(uuidMock);
        userCustom.setName("John Doe");
        userCustom.setPassword("asdF4cv3vse");
        resultOf = Optional.of(userCustom);
        authHeader = "Bearer " + this.jwtService.generateToken(userCustom);

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
    void login_ValidCredentialsAndToken_ReturnsNullPointerExceptionWithMsg() throws Exception {
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

        when(phonesRepository.findAllByUserId(userCustom.getId())).thenReturn(Collections.singletonList(phones));

        String expected = "Bad Token, Null Item";
        // Act & Assert
        NullPointerException e = assertThrows(NullPointerException.class, () ->
                authService.login(loginRequest, authHeader));
        assertEquals(expected,e.getMessage());
    }

    @Test
    void testLogin_InvalidToken_ThrowsAuthenticationException() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("userCustom@example.com");
        loginRequest.setPassword("password123");

        String authHeader = "Bearer invalid-token";

        lenient().when(validationsService.validationParams(Mockito.<String>any(), Mockito.<String>any())).thenReturn("true");

        // Forzar que verificationToken devuelva false
        doReturn(false).when(authService).verificationToken(loginRequest.getEmail(), authHeader);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest, authHeader);
        });

        assertEquals("Bad Token, Null Item", exception.getMessage());
    }
}
