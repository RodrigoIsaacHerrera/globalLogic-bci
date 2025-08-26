package org.example.service;

import org.example.web.request.LoginRequest;
import org.example.web.request.SignUpRequest;
import org.example.data.entity.Phone;
import org.example.data.entity.User;
import org.example.data.mappers.PhoneMapper;
import org.example.data.mappers.UserMapper;
import org.example.data.repository.PhonesRepository;
import org.example.data.repository.UsersRepository;
import org.example.web.reponse.LoginResponse;
import org.example.web.reponse.SignUpResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.*;

@Service
@Transactional
public class AuthService {


    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final PhonesRepository phonesRepository;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(PasswordEncoder passwordEncoder, UsersRepository usersRepository, PhonesRepository phonesRepository,
                       AuthenticationManager authManager, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
        this.phonesRepository = phonesRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public SignUpResponse signUp(SignUpRequest registerRequest) throws DuplicateKeyException {

        UUID userId = UUID.randomUUID();
        User signUser = new User();
        signUser.setId(userId);
        signUser.setName(registerRequest.getName());
        signUser.setEmail(registerRequest.getEmail());
        signUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        if (!usersRepository.existsById(userId)) {
            usersRepository.save(signUser);
            signUser.setPassword(registerRequest.getPassword());
        } else {
            throw new DuplicateKeyException(" Operation Fail. DB reject operation");
        }
        if (!registerRequest.getPhones().isEmpty()) {
            registerRequest.getPhones().forEach(phone ->
                    phonesRepository.save(new Phone(userId, phone.getNumber(), phone.getCitycode(), phone.getCountrycode()))
            );
        }
        return assemblerObjectSignUp(usersRepository
                        .findByEmailContainingIgnoreCase(signUser.getEmail()).orElseThrow(),
                registerRequest.getPhones(), registerRequest);
    }

    public LoginResponse login(LoginRequest loginRequest, String authHeader) {
        List<Phone> phones = new ArrayList<>();
        LoginResponse loginResponse;
        boolean verification = verificationToken(loginRequest.getEmail(), authHeader);
        try {
            if (verification) {
                authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                        loginRequest.getPassword()));
                User user = usersRepository.findByEmailContainingIgnoreCase(loginRequest.getEmail()).orElseThrow();
                phonesRepository.findAllByUserId(user.getId()).forEach(phone -> phones.add((Phone) phone));
                loginResponse = assemblerObjectLogin(user, phones, loginRequest);
            } else {
                throw new AuthenticationCredentialsNotFoundException("Bad Token");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return loginResponse;
    }

    private SignUpResponse assemblerObjectSignUp(User userR, List<Phone> phones, SignUpRequest signRequest) {
        String token;
        UserMapper userMapper;
        try {
            token = jwtService.generateToken(userR);
            userMapper = userMapperMethod(userR, phones);
            userMapper.setPassword(userR.getPassword()); // encriptada
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new SignUpResponse(userMapper, userR.getId().toString(),
                new Date(System.currentTimeMillis()).toString(), new Date(System.currentTimeMillis()).toString(),
                token, userR.isCredentialsNonExpired());
    }

    private LoginResponse assemblerObjectLogin(User userL, List<Phone> phones, LoginRequest loginRequest) {
        String token = jwtService.generateToken(userL);
        UserMapper user = userMapperMethod(userL, phones);
        user.setPassword(loginRequest.getPassword());
        return new LoginResponse(user.getId(), new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis()).toString(),
                token, true, user.getName(), user.getEmail(), user.getPassword(), user.getPhones());
    }

    private UserMapper userMapperMethod(User userM, List<Phone> phones) {

        UserMapper user;
        List<PhoneMapper> phoneMappers = new ArrayList<>();
        try {
            phones.forEach(phone ->
                    phoneMappers.add(new PhoneMapper(phone.getNumber(), phone.getCitycode(), phone.getCountrycode())));
            user = UserMapper.builder().id(userM.getId().toString()).name(userM.getName()).email(userM.getEmail())
                    .password(userM.getPassword())
                    .phones(phoneMappers).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    protected boolean verificationToken(String emailRequest, String authHeader) {
        String token;
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }else {
            token = authHeader;
        }
        final String idToken = this.jwtService.getIdFromToken(token);

        return this.usersRepository.findByIdAndEmail(UUID.fromString(idToken), emailRequest).isPresent()
                && emailRequest.equals(this.jwtService.getUsernameFromToken(token));


    }
}