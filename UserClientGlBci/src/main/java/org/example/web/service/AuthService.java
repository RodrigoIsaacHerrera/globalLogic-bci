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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UsersRepository usersRepository;
    private final PhonesRepository phonesRepository;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(UsersRepository usersRepository, PhonesRepository phonesRepository,
                       AuthenticationManager authManager, JwtService jwtService) {
        this.usersRepository = usersRepository;
        this.phonesRepository = phonesRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public SignUpResponse signUp(SignUpRequest registerRequest) throws DuplicateKeyException {
        UUID userId;
        User signUser = new User(registerRequest);
        userId = signUser.getId();
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
        String token = jwtService.generateRecordToken(signUpResponse);
        signUpResponse.setToken(token);
        return signUpResponse;
    }

    public LoginResponse login(AccessRequest loginRequest) {

        authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        UserDetails user = usersRepository.findByEmailContainingIgnoreCase(loginRequest.getEmail()).orElseThrow();
        String token = jwtService.generateLoginToken(user);
        // en caso de save exitoso del usuario (opcional telefono) instanciar objeto RegisteredUser
        // pendiente modificar el objeto AuthResponse (completar) de respuesta a HTTPie app
        return null; //new LoginResponse()
    }

    static SignUpResponse assemblerObjectSignUp(User userR) {

        SignUpResponse sUResponse = SignUpResponse.builder().user(userR)
                .id(userR.getId().toString())
                .created(new Date(System.currentTimeMillis()).toString())
                .lastLogin(new Date(System.currentTimeMillis()).toString())
                .token("").isActive(userR.isCredentialsNonExpired()).build();
        return sUResponse;
    }
}
