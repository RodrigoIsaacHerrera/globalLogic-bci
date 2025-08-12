package org.example.web.service;

import org.example.config.jwt.AccessRequest;
import org.example.config.jwt.SignUpRequest;
import org.example.data.entity.Phone;
import org.example.data.entity.User;
import org.example.data.repository.PhonesRepository;
import org.example.data.repository.UsersRepository;
import org.example.web.reponse.LoginResponse;
import org.example.web.reponse.SignUpResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        } else {
            throw new DuplicateKeyException(HttpStatus.CONFLICT.getReasonPhrase());
        }
        if (!registerRequest.getPhones().isEmpty()) {
            registerRequest.getPhones().forEach(phone -> {
                phonesRepository.save(new Phone(userId, phone.getNumber(), phone.getCitycode(), phone.getCountrycode()));
            });
        }
        SignUpResponse signUpResponse = assemblerObjectSignUp(usersRepository
                .findByEmailContainingIgnoreCase(signUser.getEmail()).orElseThrow());

        assert signUpResponse != null;

        return signUpResponse;
    }

    public LoginResponse login(AccessRequest loginRequest) {
        List<Phone> phones = new ArrayList<>();
        LoginResponse loginResponse;
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            User user = usersRepository.findByEmailContainingIgnoreCase(loginRequest.getEmail()).orElseThrow();
            phonesRepository.findAllByUserId(user.getId()).forEach(phone -> phones.add((Phone) phone));
            loginResponse = assemblerObjectLogin(user, phones);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return loginResponse;
    }

    private SignUpResponse assemblerObjectSignUp(User userR) {
         String token = jwtService.generateToken(userR);
        return SignUpResponse.builder().user(userR)
                .id(userR.getId().toString())
                .created(new Date(System.currentTimeMillis()).toString())
                .lastLogin(new Date(System.currentTimeMillis()).toString())
                .token(token).isActive(userR.isCredentialsNonExpired()).build();
    }
    private LoginResponse assemblerObjectLogin(User userL, List<Phone> phones) {
        String token = jwtService.generateToken(userL);
        SignUpResponse sUResponse = SignUpResponse.builder().user(userL)
                .id(userL.getId().toString())
                .created(new Date(System.currentTimeMillis()).toString())
                .lastLogin(new Date(System.currentTimeMillis()).toString())
                .token(token).isActive(userL.isCredentialsNonExpired()).build();
        return new LoginResponse(sUResponse, phones, userL);
    }
}
