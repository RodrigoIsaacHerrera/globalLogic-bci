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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
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

        //signUpResponse.setToken(token);
        return signUpResponse;
    }

    public LoginResponse login(AccessRequest loginRequest) {
        List<Phone> phones = new ArrayList<>();
        authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        User user = usersRepository.findByEmailContainingIgnoreCase(loginRequest.getEmail()).orElseThrow();
        System.out.println(user);
        phonesRepository.findAllByUserId(user.getId()).forEach(phone -> phones.add((Phone)phone));
        String token = jwtService.generateLoginToken(user);
        return assemblerObjectLogin(user, phones, token);
    }

    private SignUpResponse assemblerObjectSignUp(User userR) {
         String token = jwtService.generateRecordToken(userR);
        SignUpResponse sUResponse = SignUpResponse.builder().user(userR)
                .id(userR.getId().toString())
                .created(new Date(System.currentTimeMillis()).toString())
                .lastLogin(new Date(System.currentTimeMillis()).toString())
                .token(token).isActive(userR.isCredentialsNonExpired()).build();
        return sUResponse;
    }
    static LoginResponse assemblerObjectLogin(User userL, List<Phone> phones, String token) {
        SignUpResponse sUResponse = SignUpResponse.builder().user(userL)
                .id(userL.getId().toString())
                .created(new Date(System.currentTimeMillis()).toString())
                .lastLogin(new Date(System.currentTimeMillis()).toString())
                .token(token).isActive(userL.isCredentialsNonExpired()).build();
        return new LoginResponse(sUResponse, phones, userL);
    }
}
