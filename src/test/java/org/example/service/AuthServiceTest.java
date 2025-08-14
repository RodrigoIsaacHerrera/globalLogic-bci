package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.example.config.jwt.JwtService;
import org.example.data.entity.Phone;
import org.example.data.entity.User;
import org.example.data.mappers.UserMapper;
import org.example.data.repository.PhonesRepository;
import org.example.data.repository.UsersRepository;
import org.example.web.reponse.LoginResponse;
import org.example.web.reponse.SignUpResponse;
import org.example.web.request.LoginRequest;
import org.example.web.request.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenThrow(new DuplicateKeyException("Msg"));
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<User>any())).thenReturn(user);

        SignUpRequest registerRequest = new SignUpRequest();
        registerRequest.setPhones(new ArrayList<>());

        // Act and Assert
        assertThrows(DuplicateKeyException.class, () -> authService.signUp(registerRequest));
        verify(usersRepository).existsById(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase(null);
        verify(usersRepository).save(isA(User.class));
    }

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Given {@link JwtService} {@link JwtService#generateToken(User)} throw {@link
     *       DuplicateKeyException#DuplicateKeyException(String)} with {@code Msg}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName(
            "Test signUp(SignUpRequest); given JwtService generateToken(User) throw DuplicateKeyException(String) with 'Msg'")
    void testSignUp_givenJwtServiceGenerateTokenThrowDuplicateKeyExceptionWithMsg()
            throws DuplicateKeyException {
        // Arrange
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");

        User user2 = new User();
        user2.setEmail("jane.doe@example.org");
        user2.setId(UUID.randomUUID());
        user2.setName("Name");
        user2.setPassword("iloveyou");
        Optional<User> ofResult = Optional.of(user2);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<User>any())).thenReturn(user);
        when(jwtService.generateToken(Mockito.<User>any())).thenThrow(new DuplicateKeyException("Msg"));

        SignUpRequest registerRequest = new SignUpRequest();
        registerRequest.setPhones(new ArrayList<>());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> authService.signUp(registerRequest));
        verify(jwtService).generateToken(isA(User.class));
        verify(usersRepository).existsById(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase(null);
        verify(usersRepository).save(isA(User.class));
    }

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Given of {@link User}.
     *   <li>When {@link SignUpRequest#SignUpRequest()} Phones is {@link ArrayList#ArrayList()}.
     *   <li>Then calls {@link PhonesRepository#save(Object)}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName(
            "Test signUp(SignUpRequest); given of User; when SignUpRequest() Phones is ArrayList(); then calls save(Object)")
    void testSignUp_givenOfUser_whenSignUpRequestPhonesIsArrayList_thenCallsSave()
            throws DuplicateKeyException {
        // Arrange
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<User>any())).thenReturn(user);
        User user2 = mock(User.class);
        doNothing().when(user2).setEmail(Mockito.<String>any());
        doNothing().when(user2).setId(Mockito.<UUID>any());
        doNothing().when(user2).setName(Mockito.<String>any());
        doNothing().when(user2).setPassword(Mockito.<String>any());
        user2.setEmail("jane.doe@example.org");
        user2.setId(UUID.randomUUID());
        user2.setName("Name");
        user2.setPassword("iloveyou");
        Optional.of(user2);
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
        verify(user2).setEmail("jane.doe@example.org");
        verify(user2).setId(isA(UUID.class));
        verify(user2).setName("Name");
        verify(user2).setPassword("iloveyou");
        verify(usersRepository).existsById(isA(UUID.class));
        verify(phonesRepository).save(isA(Phone.class));
        verify(usersRepository).save(isA(User.class));
    }

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Given {@link PhonesRepository} {@link PhonesRepository#save(Object)} return {@link
     *       Phone#Phone()}.
     *   <li>Then calls {@link User#getId()}.
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
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");
        User user2 = mock(User.class);
        when(user2.getName()).thenThrow(new DuplicateKeyException("Msg"));
        when(user2.getId()).thenReturn(UUID.randomUUID());
        doNothing().when(user2).setEmail(Mockito.<String>any());
        doNothing().when(user2).setId(Mockito.<UUID>any());
        doNothing().when(user2).setName(Mockito.<String>any());
        doNothing().when(user2).setPassword(Mockito.<String>any());
        user2.setEmail("jane.doe@example.org");
        user2.setId(UUID.randomUUID());
        user2.setName("Name");
        user2.setPassword("iloveyou");
        Optional<User> ofResult = Optional.of(user2);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<User>any())).thenReturn(user);

        Phone phone = new Phone();
        phone.setCitycode(1);
        phone.setCountrycode("GB");
        phone.setId(1L);
        phone.setNumber(1L);
        phone.setUserId(UUID.randomUUID());
        when(phonesRepository.save(Mockito.<Phone>any())).thenReturn(phone);
        when(jwtService.generateToken(Mockito.<User>any())).thenReturn("ABC123");

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
        verify(jwtService).generateToken(isA(User.class));
        verify(user2).getId();
        verify(user2).getName();
        verify(user2).setEmail("jane.doe@example.org");
        verify(user2).setId(isA(UUID.class));
        verify(user2).setName("Name");
        verify(user2).setPassword("iloveyou");
        verify(usersRepository).existsById(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase(null);
        verify(phonesRepository).save(isA(Phone.class));
        verify(usersRepository).save(isA(User.class));
    }

    /**
     * Test {@link AuthService#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Given {@link User} {@link User#getId()} return randomUUID.
     *   <li>Then calls {@link User#getId()}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName(
            "Test signUp(SignUpRequest); given User getId() return randomUUID; then calls getId()")
    void testSignUp_givenUserGetIdReturnRandomUUID_thenCallsGetId() throws DuplicateKeyException {
        // Arrange
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");
        User user2 = mock(User.class);
        when(user2.getName()).thenThrow(new DuplicateKeyException("Msg"));
        when(user2.getId()).thenReturn(UUID.randomUUID());
        doNothing().when(user2).setEmail(Mockito.<String>any());
        doNothing().when(user2).setId(Mockito.<UUID>any());
        doNothing().when(user2).setName(Mockito.<String>any());
        doNothing().when(user2).setPassword(Mockito.<String>any());
        user2.setEmail("jane.doe@example.org");
        user2.setId(UUID.randomUUID());
        user2.setName("Name");
        user2.setPassword("iloveyou");
        Optional<User> ofResult = Optional.of(user2);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<User>any())).thenReturn(user);
        when(jwtService.generateToken(Mockito.<User>any())).thenReturn("ABC123");

        SignUpRequest registerRequest = new SignUpRequest();
        registerRequest.setPhones(new ArrayList<>());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> authService.signUp(registerRequest));
        verify(jwtService).generateToken(isA(User.class));
        verify(user2).getId();
        verify(user2).getName();
        verify(user2).setEmail("jane.doe@example.org");
        verify(user2).setId(isA(UUID.class));
        verify(user2).setName("Name");
        verify(user2).setPassword("iloveyou");
        verify(usersRepository).existsById(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase(null);
        verify(usersRepository).save(isA(User.class));
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
            "Test signUp(SignUpRequest); given UsersRepository existsById(UUID) throw DuplicateKeyException(String) with 'Msg'")
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
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");

        User user2 = new User();
        user2.setEmail("jane.doe@example.org");
        user2.setId(UUID.randomUUID());
        user2.setName("Name");
        user2.setPassword("iloveyou");
        Optional<User> ofResult = Optional.of(user2);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(usersRepository.existsById(Mockito.<UUID>any())).thenReturn(false);
        when(usersRepository.save(Mockito.<User>any())).thenReturn(user);
        when(jwtService.generateToken(Mockito.<User>any())).thenReturn("ABC123");

        SignUpRequest registerRequest = new SignUpRequest();
        registerRequest.setPhones(new ArrayList<>());

        // Act
        SignUpResponse actualSignUpResult = authService.signUp(registerRequest);

        // Assert
        verify(jwtService).generateToken(isA(User.class));
        verify(usersRepository).existsById(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase(null);
        verify(usersRepository).save(isA(User.class));
        assertEquals("ABC123", actualSignUpResult.getToken());
        UserMapper user3 = actualSignUpResult.getUser();
        assertEquals("Name", user3.getName());
        assertEquals("iloveyou", user3.getPassword());
        assertEquals("jane.doe@example.org", user3.getEmail());
        assertTrue(user3.getPhones().isEmpty());
        assertTrue(actualSignUpResult.isActive());
    }

    /**
     * Test {@link AuthService#login(LoginRequest)}.
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest)}
     */
    @Test
    @DisplayName("Test login(LoginRequest)")
    void testLogin() {
        // Arrange
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenThrow(new DuplicateKeyException("Msg"));

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> authService.login(new LoginRequest("jane.doe@example.org", "iloveyou")));
        verify(usersRepository).findByEmailContainingIgnoreCase("jane.doe@example.org");
    }

    /**
     * Test {@link AuthService#login(LoginRequest)}.
     *
     * <ul>
     *   <li>Given {@link ArrayList#ArrayList()} add {@link RunAsImplAuthenticationProvider} (default
     *       constructor).
     * </ul>
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest)}
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
                () -> authService.login(new LoginRequest("jane.doe@example.org", "iloveyou")));
    }

    /**
     * Test {@link AuthService#login(LoginRequest)}.
     *
     * <ul>
     *   <li>Given {@link JwtService} {@link JwtService#generateToken(User)} throw {@link
     *       DuplicateKeyException#DuplicateKeyException(String)} with {@code Msg}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest)}
     */
    @Test
    @DisplayName(
            "Test login(LoginRequest); given JwtService generateToken(User) throw DuplicateKeyException(String) with 'Msg'")
    void testLogin_givenJwtServiceGenerateTokenThrowDuplicateKeyExceptionWithMsg() {
        // Arrange
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");
        Optional<User> ofResult = Optional.of(user);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(phonesRepository.findAllByUserId(Mockito.<UUID>any())).thenReturn(new ArrayList<>());
        when(jwtService.generateToken(Mockito.<User>any())).thenThrow(new DuplicateKeyException("Msg"));

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> authService.login(new LoginRequest("jane.doe@example.org", "iloveyou")));
        verify(jwtService).generateToken(isA(User.class));
        verify(phonesRepository).findAllByUserId(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase("jane.doe@example.org");
    }

    /**
     * Test {@link AuthService#login(LoginRequest)}.
     *
     * <ul>
     *   <li>Given {@link PhonesRepository} {@link PhonesRepository#findAllByUserId(UUID)} throw
     *       {@link DuplicateKeyException#DuplicateKeyException(String)} with {@code Msg}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest)}
     */
    @Test
    @DisplayName(
            "Test login(LoginRequest); given PhonesRepository findAllByUserId(UUID) throw DuplicateKeyException(String) with 'Msg'")
    void testLogin_givenPhonesRepositoryFindAllByUserIdThrowDuplicateKeyExceptionWithMsg() {
        // Arrange
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");
        Optional<User> ofResult = Optional.of(user);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(phonesRepository.findAllByUserId(Mockito.<UUID>any()))
                .thenThrow(new DuplicateKeyException("Msg"));

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> authService.login(new LoginRequest("jane.doe@example.org", "iloveyou")));
        verify(phonesRepository).findAllByUserId(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase("jane.doe@example.org");
    }

    /**
     * Test {@link AuthService#login(LoginRequest)}.
     *
     * <ul>
     *   <li>Given {@link User#User()} Email is {@code jane.doe@example.org}.
     *   <li>Then return Token is {@code ABC123}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest)}
     */
    @Test
    @DisplayName(
            "Test login(LoginRequest); given User() Email is 'jane.doe@example.org'; then return Token is 'ABC123'")
    void testLogin_givenUserEmailIsJaneDoeExampleOrg_thenReturnTokenIsAbc123() {
        // Arrange
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");
        Optional<User> ofResult = Optional.of(user);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(phonesRepository.findAllByUserId(Mockito.<UUID>any())).thenReturn(new ArrayList<>());
        when(jwtService.generateToken(Mockito.<User>any())).thenReturn("ABC123");

        // Act
        LoginResponse actualLoginResult =
                authService.login(new LoginRequest("jane.doe@example.org", "iloveyou"));

        // Assert
        verify(jwtService).generateToken(isA(User.class));
        verify(phonesRepository).findAllByUserId(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase("jane.doe@example.org");
        assertEquals("ABC123", actualLoginResult.getToken());
        assertEquals("Name", actualLoginResult.getName());
        assertEquals("iloveyou", actualLoginResult.getPassword());
        assertEquals("jane.doe@example.org", actualLoginResult.getEmail());
        assertTrue(actualLoginResult.getPhones().isEmpty());
        assertTrue(actualLoginResult.isActive());
    }

    /**
     * Test {@link AuthService#login(LoginRequest)}.
     *
     * <ul>
     *   <li>Given {@link User} {@link User#getId()} return randomUUID.
     *   <li>Then calls {@link User#getId()}.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest)}
     */
    @Test
    @DisplayName("Test login(LoginRequest); given User getId() return randomUUID; then calls getId()")
    void testLogin_givenUserGetIdReturnRandomUUID_thenCallsGetId() {
        // Arrange
        User user = mock(User.class);
        when(user.getName()).thenThrow(new DuplicateKeyException("Msg"));
        when(user.getId()).thenReturn(UUID.randomUUID());
        doNothing().when(user).setEmail(Mockito.<String>any());
        doNothing().when(user).setId(Mockito.<UUID>any());
        doNothing().when(user).setName(Mockito.<String>any());
        doNothing().when(user).setPassword(Mockito.<String>any());
        user.setEmail("jane.doe@example.org");
        user.setId(UUID.randomUUID());
        user.setName("Name");
        user.setPassword("iloveyou");
        Optional<User> ofResult = Optional.of(user);
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(ofResult);
        when(phonesRepository.findAllByUserId(Mockito.<UUID>any())).thenReturn(new ArrayList<>());
        when(jwtService.generateToken(Mockito.<User>any())).thenReturn("ABC123");

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> authService.login(new LoginRequest("jane.doe@example.org", "iloveyou")));
        verify(jwtService).generateToken(isA(User.class));
        verify(user, atLeast(1)).getId();
        verify(user).getName();
        verify(user).setEmail("jane.doe@example.org");
        verify(user).setId(isA(UUID.class));
        verify(user).setName("Name");
        verify(user).setPassword("iloveyou");
        verify(phonesRepository).findAllByUserId(isA(UUID.class));
        verify(usersRepository).findByEmailContainingIgnoreCase("jane.doe@example.org");
    }

    /**
     * Test {@link AuthService#login(LoginRequest)}.
     *
     * <ul>
     *   <li>Given {@link UsersRepository} {@link
     *       UsersRepository#findByEmailContainingIgnoreCase(String)} return empty.
     * </ul>
     *
     * <p>Method under test: {@link AuthService#login(LoginRequest)}
     */
    @Test
    @DisplayName(
            "Test login(LoginRequest); given UsersRepository findByEmailContainingIgnoreCase(String) return empty")
    void testLogin_givenUsersRepositoryFindByEmailContainingIgnoreCaseReturnEmpty() {
        // Arrange
        Optional<User> emptyResult = Optional.empty();
        when(usersRepository.findByEmailContainingIgnoreCase(Mockito.<String>any()))
                .thenReturn(emptyResult);

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> authService.login(new LoginRequest("jane.doe@example.org", "iloveyou")));
        verify(usersRepository).findByEmailContainingIgnoreCase("jane.doe@example.org");
    }
}
