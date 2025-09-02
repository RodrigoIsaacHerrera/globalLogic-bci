package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.example.data.entity.Phone;
import org.example.data.entity.UserCustom;
import org.example.data.mappers.UserMapper;
import org.example.data.repository.PhonesRepository;
import org.example.data.repository.UsersRepository;
import org.example.web.reponse.SignUpResponse;
import org.example.web.request.LoginRequest;
import org.example.web.request.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {AuthService.class})
@ExtendWith(SpringExtension.class)
class AuthServiceTest {
    @Autowired
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

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <p>Method under test: {@link AuthService#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName("Test signUp(SignUpRequest)")
    void testSignUp() throws DuplicateKeyException {
        // Arrange
        UserCustom userCustom = new UserCustom();
        userCustom.setEmail("jane.doe@example.org");
        userCustom.setId(UUID.randomUUID());
        userCustom.setName("Name");
        userCustom.setPassword("iloveyou");
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenThrow(new DuplicateKeyException("Msg"));
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<UserCustom>any())).thenReturn(userCustom);

        SignUpRequest registerRequest = new SignUpRequest();
        registerRequest.setPhones(new ArrayList<>());

        // Act and Assert
        assertThrows(DuplicateKeyException.class, () -> authService.signUp(registerRequest));
        verify(usersRepository).existsById(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase(null);
        verify(usersRepository).save(isA(UserCustom.class));
    }

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Given {@link JwtService} {@link JwtService#generateToken(UserCustom)} throw {@link
     *       DuplicateKeyException#DuplicateKeyException(String)} with {@code Msg}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName(
            "Test signUp(SignUpRequest); given JwtService generateToken(UserCustom) throw DuplicateKeyException(String) with 'Msg'")
    void testSignUp_givenJwtServiceGenerateTokenThrowDuplicateKeyExceptionWithMsg()
            throws DuplicateKeyException {
        // Arrange
        UserCustom userCustom = new UserCustom();
        userCustom.setEmail("jane.doe@example.org");
        userCustom.setId(UUID.randomUUID());
        userCustom.setName("Name");
        userCustom.setPassword("iloveyou");

        UserCustom userCustom2 = new UserCustom();
        userCustom2.setEmail("jane.doe@example.org");
        userCustom2.setId(UUID.randomUUID());
        userCustom2.setName("Name");
        userCustom2.setPassword("iloveyou");
        Optional<UserCustom> ofResult = Optional.of(userCustom2);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<UserCustom>any())).thenReturn(userCustom);
        when(jwtService.generateToken(Mockito.<UserCustom>any())).thenThrow(new DuplicateKeyException("Msg"));

        SignUpRequest registerRequest = new SignUpRequest();
        registerRequest.setPhones(new ArrayList<>());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> authService.signUp(registerRequest));
        verify(jwtService).generateToken(isA(UserCustom.class));
        verify(usersRepository).existsById(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase(null);
        verify(usersRepository).save(isA(UserCustom.class));
    }

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Given of {@link UserCustom}.
     *   <li>When {@link SignUpRequest#SignUpRequest()} Phones is {@link ArrayList#ArrayList()}.
     *   <li>Then calls {@link PhonesRepository#save(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName(
            "Test signUp(SignUpRequest); given of UserCustom; when SignUpRequest() Phones is ArrayList(); then calls save(Object)")
    void testSignUp_givenOfUser_whenSignUpRequestPhonesIsArrayList_thenCallsSave()
            throws DuplicateKeyException {
        // Arrange
        UserCustom userCustom = new UserCustom();
        userCustom.setEmail("jane.doe@example.org");
        userCustom.setId(UUID.randomUUID());
        userCustom.setName("Name");
        userCustom.setPassword("iloveyou");
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<UserCustom>any())).thenReturn(userCustom);
        UserCustom userCustom2 = mock(UserCustom.class);
        doNothing().when(userCustom2).setEmail(Mockito.<String>any());
        doNothing().when(userCustom2).setId(Mockito.<UUID>any());
        doNothing().when(userCustom2).setName(Mockito.<String>any());
        doNothing().when(userCustom2).setPassword(Mockito.<String>any());
        userCustom2.setEmail("jane.doe@example.org");
        userCustom2.setId(UUID.randomUUID());
        userCustom2.setName("Name");
        userCustom2.setPassword("iloveyou");
        Optional.of(userCustom2);
        when(phonesRepository.save(Mockito.<Phone>any())).thenThrow(new DuplicateKeyException("Msg"));

        Phone phone = new Phone();
        phone.setCitycode(1);
        phone.setCountrycode("GB");
        phone.setId(1L);
        phone.setNumber(1L);
        phone.setUserId(UUID.randomUUID());

        ArrayList<Phone> phones = new ArrayList<>();
        phones.add(phone);

        SignUpRequest registerRequest = new SignUpRequest();
        registerRequest.setPhones(phones);

        // Act and Assert
        assertThrows(DuplicateKeyException.class, () -> authService.signUp(registerRequest));
        verify(userCustom2).setEmail("jane.doe@example.org");
        verify(userCustom2).setId(isA(UUID.class));
        verify(userCustom2).setName("Name");
        verify(userCustom2).setPassword("iloveyou");
        verify(usersRepository).existsById(isA(UUID.class));
        verify(phonesRepository).save(isA(Phone.class));
        verify(usersRepository).save(isA(UserCustom.class));
    }

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Given {@link PhonesRepository} {@link PhonesRepository#save(Object)} return {@link
     *       Phone#Phone()}.
     *   <li>Then calls {@link UserCustom#getId()}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName(
            "Test signUp(SignUpRequest); given PhonesRepository save(Object) return Phone(); then calls getId()")
    void testSignUp_givenPhonesRepositorySaveReturnPhone_thenCallsGetId()
            throws DuplicateKeyException {
        // Arrange
        UserCustom userCustom = new UserCustom();
        userCustom.setEmail("jane.doe@example.org");
        userCustom.setId(UUID.randomUUID());
        userCustom.setName("Name");
        userCustom.setPassword("iloveyou");
        UserCustom userCustom2 = mock(UserCustom.class);
        when(userCustom2.getName()).thenThrow(new DuplicateKeyException("Msg"));
        when(userCustom2.getId()).thenReturn(UUID.randomUUID());
        doNothing().when(userCustom2).setEmail(Mockito.<String>any());
        doNothing().when(userCustom2).setId(Mockito.<UUID>any());
        doNothing().when(userCustom2).setName(Mockito.<String>any());
        doNothing().when(userCustom2).setPassword(Mockito.<String>any());
        userCustom2.setEmail("jane.doe@example.org");
        userCustom2.setId(UUID.randomUUID());
        userCustom2.setName("Name");
        userCustom2.setPassword("iloveyou");
        Optional<UserCustom> ofResult = Optional.of(userCustom2);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<UserCustom>any())).thenReturn(userCustom);

        Phone phone = new Phone();
        phone.setCitycode(1);
        phone.setCountrycode("GB");
        phone.setId(1L);
        phone.setNumber(1L);
        phone.setUserId(UUID.randomUUID());
        when(phonesRepository.save(Mockito.<Phone>any())).thenReturn(phone);
        when(jwtService.generateToken(Mockito.<UserCustom>any())).thenReturn("ABC123");

        Phone phone2 = new Phone();
        phone2.setCitycode(1);
        phone2.setCountrycode("GB");
        phone2.setId(1L);
        phone2.setNumber(1L);
        phone2.setUserId(UUID.randomUUID());

        ArrayList<Phone> phones = new ArrayList<>();
        phones.add(phone2);

        SignUpRequest registerRequest = new SignUpRequest();
        registerRequest.setPhones(phones);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> authService.signUp(registerRequest));
        verify(jwtService).generateToken(isA(UserCustom.class));
        verify(userCustom2).getId();
        verify(userCustom2).getName();
        verify(userCustom2).setEmail("jane.doe@example.org");
        verify(userCustom2).setId(isA(UUID.class));
        verify(userCustom2).setName("Name");
        verify(userCustom2).setPassword("iloveyou");
        verify(usersRepository).existsById(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase(null);
        verify(phonesRepository).save(isA(Phone.class));
        verify(usersRepository).save(isA(UserCustom.class));
    }

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Given {@link UserCustom} {@link UserCustom#getId()} return randomUUID.
     *   <li>Then calls {@link UserCustom#getId()}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName(
            "Test signUp(SignUpRequest); given UserCustom getId() return randomUUID; then calls getId()")
    void testSignUp_givenUserGetIdReturnRandomUUID_thenCallsGetId() throws DuplicateKeyException {
        // Arrange
        UserCustom userCustom = new UserCustom();
        userCustom.setEmail("jane.doe@example.org");
        userCustom.setId(UUID.randomUUID());
        userCustom.setName("Name");
        userCustom.setPassword("iloveyou");
        UserCustom userCustom2 = mock(UserCustom.class);
        when(userCustom2.getName()).thenThrow(new DuplicateKeyException("Msg"));
        when(userCustom2.getId()).thenReturn(UUID.randomUUID());
        doNothing().when(userCustom2).setEmail(Mockito.<String>any());
        doNothing().when(userCustom2).setId(Mockito.<UUID>any());
        doNothing().when(userCustom2).setName(Mockito.<String>any());
        doNothing().when(userCustom2).setPassword(Mockito.<String>any());
        userCustom2.setEmail("jane.doe@example.org");
        userCustom2.setId(UUID.randomUUID());
        userCustom2.setName("Name");
        userCustom2.setPassword("iloveyou");
        Optional<UserCustom> ofResult = Optional.of(userCustom2);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<UserCustom>any())).thenReturn(userCustom);
        when(jwtService.generateToken(Mockito.<UserCustom>any())).thenReturn("ABC123");

        SignUpRequest registerRequest = new SignUpRequest();
        registerRequest.setPhones(new ArrayList<>());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> authService.signUp(registerRequest));
        verify(jwtService).generateToken(isA(UserCustom.class));
        verify(userCustom2).getId();
        verify(userCustom2).getName();
        verify(userCustom2).setEmail("jane.doe@example.org");
        verify(userCustom2).setId(isA(UUID.class));
        verify(userCustom2).setName("Name");
        verify(userCustom2).setPassword("iloveyou");
        verify(usersRepository).existsById(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase(null);
        verify(usersRepository).save(isA(UserCustom.class));
    }

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Given {@link UsersRepository} {@link UsersRepository#existsById(UUID)} return {@code
     *       true}.
     *   <li>When {@link SignUpRequest#SignUpRequest()}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName(
            "Test signUp(SignUpRequest); given UsersRepository existsById(UUID) return 'true'; when SignUpRequest()")
    void testSignUp_givenUsersRepositoryExistsByIdReturnTrue_whenSignUpRequest()
            throws DuplicateKeyException {
        // Arrange
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(true);

        // Act and Assert
        assertThrows(DuplicateKeyException.class, () -> authService.signUp(new SignUpRequest()));
        verify(usersRepository).existsById(isA(UUID.class));
    }

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Given {@link UsersRepository} {@link UsersRepository#existsById(UUID)} throw {@link
     *       DuplicateKeyException#DuplicateKeyException(String)} with {@code Msg}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName(
            "Test signUp(SignUpRequest); given UsersRepository existsById(UUID)" +
                    " throw DuplicateKeyException(String) with 'Msg'")
    void testSignUp_givenUsersRepositoryExistsByIdThrowDuplicateKeyExceptionWithMsg()
            throws DuplicateKeyException {
        // Arrange
        when(usersRepository.existsById(Mockito.<UUID>any()))
                .thenThrow(new DuplicateKeyException("Msg"));

        // Act and Assert
        assertThrows(DuplicateKeyException.class, () -> authService.signUp(new SignUpRequest()));
        verify(usersRepository).existsById(isA(UUID.class));
    }

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Then return Token is {@code ABC123}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName("Test signUp(SignUpRequest); then return Token is 'ABC123'")
    void testSignUp_thenReturnTokenIsAbc123() throws DuplicateKeyException {
        // Arrange
        UserCustom userCustom = new UserCustom();
        userCustom.setEmail("jane.doe@example.org");
        userCustom.setId(UUID.randomUUID());
        userCustom.setName("Name");
        userCustom.setPassword("iloveyou");

        UserCustom userCustom2 = new UserCustom();
        userCustom2.setEmail("jane.doe@example.org");
        userCustom2.setId(UUID.randomUUID());
        userCustom2.setName("Name");
        userCustom2.setPassword("iloveyou");
        Optional<UserCustom> ofResult = Optional.of(userCustom2);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<UserCustom>any())).thenReturn(userCustom);
        when(jwtService.generateToken(Mockito.<UserCustom>any())).thenReturn("ABC123");

        SignUpRequest registerRequest = new SignUpRequest();
        registerRequest.setPhones(new ArrayList<>());

        // Act
        SignUpResponse actualSignUpResult = authService.signUp(registerRequest);

        // Assert
        verify(jwtService).generateToken(isA(UserCustom.class));
        verify(usersRepository).existsById(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase(null);
        verify(usersRepository).save(isA(UserCustom.class));
        assertEquals("ABC123", actualSignUpResult.getToken());
        UserMapper user3 = actualSignUpResult.getUser();
        assertEquals("Name", user3.getName());
        assertEquals("iloveyou", user3.getPassword());
        assertEquals("jane.doe@example.org", user3.getEmail());
        assertTrue(user3.getPhones().isEmpty());
        assertTrue(actualSignUpResult.isActive());
    }

    /**
     * Test {@link AuthService#login(LoginRequest, String)}.
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest, String)}
     */

    /**
     * Test {@link AuthService#login(LoginRequest, String)}.
     *
     * <ul>
     *   <li>Given {@link ArrayList#ArrayList()} add {@link RunAsImplAuthenticationProvider} (default
     *       constructor).
     * </ul>
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest, String)}
     */
    @Test
    @DisplayName(
            "Test login(LoginRequest); given ArrayList() add RunAsImplAuthenticationProvider (default constructor)")
    void testLogin_givenArrayListAddRunAsImplAuthenticationProvider() {
        // Arrange
        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authManager = new ProviderManager(providers);
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        UsersRepository usersRepository = mock(UsersRepository.class);
        PhonesRepository phonesRepository = mock(PhonesRepository.class);
        AuthService authService =
                new AuthService(
                        passwordEncoder, usersRepository, phonesRepository, authManager, new JwtService());

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> authService.login(new LoginRequest("jane.doe@example.org"
                        , "iloveyou"), "Bearer "));
    }

    /**
     * Test {@link AuthService#login(LoginRequest, String)}.
     *
     * <ul>
     *   <li>Given {@link JwtService} {@link JwtService#generateToken(UserCustom)} throw {@link
     *       DuplicateKeyException#DuplicateKeyException(String)} with {@code Msg}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest, String)}
     */
    @Test
    @DisplayName(
            "Test login(LoginRequest, authHeader); given JwtService generateToken(UserCustom)" +
                    " throw DuplicateKeyException(String) with 'Msg'")
    public void testLogin_givenJwtServiceGenerateTokenThrowNullPointerException() {
        // Arrange
        UserCustom userCustom = new UserCustom();
        userCustom.setEmail("jane.doe@example.org");
        userCustom.setId(UUID.fromString("ef199728-21aa-4a3c-a846-66202c1866c1"));
        userCustom.setName("Name");
        userCustom.setPassword("iloveyou");

        Optional<UserCustom> ofResult = Optional.of(userCustom);
        when(usersRepository.findByEmailContainingIgnoreCase("jane.doe@example.org"))
                .thenReturn(ofResult);
        when(phonesRepository.findAllByUserId(UUID.fromString("ef199728-21aa-4a3c-a846-66202c1866c1")))
                .thenReturn(new ArrayList<>());
        when(jwtService.generateToken(userCustom)).thenThrow(new NullPointerException());
        when(jwtService.getIdFromToken("")).thenReturn("ef199728-21aa-4a3c-a846-66202c1866c1");

        // Act and Assert

        assertThrows(
                RuntimeException.class,
                () -> authService
                        .login(new LoginRequest("jane.doe@example.org", "iloveyou")
                                , "Bearer "));
    }

    /**
     * Test {@link AuthService#login(LoginRequest, String)}.
     *
     * <ul>
     *   <li>Given {@link PhonesRepository} {@link PhonesRepository#findAllByUserId(UUID)} throw
     *       {@link DuplicateKeyException#DuplicateKeyException(String)} with {@code Msg}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest, String)}
     */
    @Test
    @DirtiesContext
    @DisplayName(
            "Test login(LoginRequest, AuthHeader); given PhonesRepository findAllByUserId(UUID) " +
                    "throw DuplicateKeyException(String) with 'Msg'")
    public void testLogin_givenPhonesRepositoryFindAllByUserIdThrowDuplicateKeyExceptionWithMsg() {
        // Arrange
        UserCustom userCustom = new UserCustom();
        userCustom.setEmail("jane.doe@example.org");
        userCustom.setId(UUID.fromString("ef199728-21aa-4a3c-a846-66202c1866c1"));
        userCustom.setName("Name");
        userCustom.setPassword("iloveyou");
        Optional<UserCustom> ofResult = Optional.of(userCustom);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(phonesRepository.findAllByUserId(UUID.fromString("ef199728-21aa-4a3c-a846-66202c1866c1")))
                .thenThrow(new DuplicateKeyException("Msg"));

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> authService.login(new LoginRequest("jane.doe@example.org", "iloveyou"), "Bearer "));
    }


    /**
     * Test {@link AuthService#login(LoginRequest, String)}.
     *
     * <ul>
     *   <li>Given {@link UserCustom} {@link UserCustom#getId()} return randomUUID.
     *   <li>Then calls {@link UserCustom#getId()}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest, String)}
     */
    @Test
    @DisplayName("Test login(LoginRequest); given UserCustom getId() return randomUUID; then calls getId()")
    void testLogin_givenUserGetIdReturnRandomUUID_thenCallsGetId() {
        // Arrange
        UserCustom userCustom = mock(UserCustom.class);
        when(userCustom.getName()).thenThrow(new DuplicateKeyException("Msg"));
        userCustom.setEmail("jane.doe@example.org");
        userCustom.setId(UUID.randomUUID());
        userCustom.setName("Name");
        userCustom.setPassword("iloveyou");
        String token = jwtService.generateToken(userCustom);
        Optional<UserCustom> ofResult = Optional.of(userCustom);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(phonesRepository.findAllByUserId(Mockito.<UUID>any())).thenReturn(new ArrayList<>());
        when(jwtService.generateToken(Mockito.<UserCustom>any())).thenReturn(token);

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> authService.login(new LoginRequest(Mockito.any(), Mockito.any()),
                        "Bearer " + token));
        verify(jwtService).generateToken(isA(UserCustom.class));

    }

    /**
     * Test {@link AuthService#login(LoginRequest, String)}.
     *
     * <ul>
     *   <li>Given {@link UsersRepository} {@link
     *       UsersRepository#findByEmailContainingIgnoreCase(String)} return empty.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest, String)}
     */
    @Test
    @DisplayName(
            "Test login(LoginRequest, AuthHeader); given UsersRepository findByEmailContainingIgnoreCase(String) " +
                    "return empty")
    void testLogin_givenUsersRepositoryFindByEmailContainingIgnoreCaseReturnEmpty() {
        // Arrange
        Optional<UserCustom> emptyResult = Optional.empty();
        when(usersRepository.findByEmailContainingIgnoreCase("jane.doe@example.org"))
                .thenReturn(emptyResult);

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> authService.login(new LoginRequest("jane.doe@example.org", "iloveyou"),
                        "Bearer "));
    }
}