package org.example.service;

import org.example.data.entity.UserCustom;
import org.example.web.request.LoginRequest;
import org.example.web.request.SignUpRequest;
import org.example.data.entity.Phone;
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


    private final ValidationsService validationsService;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final PhonesRepository phonesRepository;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(ValidationsService validationsService, PasswordEncoder passwordEncoder, UsersRepository usersRepository, PhonesRepository phonesRepository,
                       AuthenticationManager authManager, JwtService jwtService) {
        this.validationsService = validationsService;
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
        this.phonesRepository = phonesRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public SignUpResponse signUp(SignUpRequest registerRequest) {

        UUID userId = UUID.randomUUID();
        UserCustom signUserCustom = new UserCustom();

        try {
            validationParams(registerRequest);
            signUserCustom.setId(userId);
            signUserCustom.setName(registerRequest.getName());
            signUserCustom.setEmail(registerRequest.getEmail());
            signUserCustom.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            if (!usersRepository.existsById(userId)) {
                usersRepository.save(signUserCustom);
                signUserCustom.setPassword(registerRequest.getPassword());
            } else {
                throw new DuplicateKeyException(" Operation Fail. DB reject operation");
            }
            if (!registerRequest.getPhones().isEmpty()) {
                registerRequest.getPhones().forEach(phone ->
                        phonesRepository.save(new Phone(userId, phone.getNumber(), phone.getCitycode(), phone.getCountrycode()))
                );
            }

        }catch (DuplicateKeyException e){
            throw new DuplicateKeyException("", e);
        }catch (IllegalArgumentException e){
            throw e;
        }

        return assemblerObjectSignUp(usersRepository
                        .findByEmailContainingIgnoreCase(signUserCustom.getEmail()).orElseThrow(),
                registerRequest.getPhones());
    }

    public LoginResponse login(LoginRequest loginRequest, String authHeader){
        List<Phone> phones = new ArrayList<>();
        LoginResponse loginResponse;
        boolean verification = verificationToken(loginRequest.getEmail(), authHeader);

        try {
            validationParams(loginRequest);
            if (verification) {
                authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                        loginRequest.getPassword()));
                UserCustom userCustom = usersRepository.findByEmailContainingIgnoreCase(loginRequest.getEmail()).orElseThrow();
                phonesRepository.findAllByUserId(userCustom.getId()).forEach(phone -> phones.add((Phone) phone));
                loginResponse = assemblerObjectLogin(userCustom, phones);
            } else {
                throw new AuthenticationCredentialsNotFoundException("Bad Token");
            }

        } catch (AuthenticationCredentialsNotFoundException e) {
            throw new AuthenticationCredentialsNotFoundException("Bad Token", e);
        } catch (NullPointerException e) {
            throw new NullPointerException("Bad Token, Null Item");
        } catch (IllegalArgumentException e){
            throw e;
        }


        return loginResponse;
    }

    private SignUpResponse assemblerObjectSignUp(UserCustom userCustomR, List<Phone> phones) {
        String token;
        UserMapper userMapper;
        try {
            token = jwtService.generateToken(userCustomR);
            userMapper = userMapperMethod(userCustomR, phones);
            userMapper.setPassword(userCustomR.getPassword()); // encriptada
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new SignUpResponse(userMapper, userCustomR.getId().toString(),
                new Date(System.currentTimeMillis()).toString(), new Date(System.currentTimeMillis()).toString(),
                token, userCustomR.isCredentialsNonExpired());
    }

    private LoginResponse assemblerObjectLogin(UserCustom userCustomL, List<Phone> phones) {
        String token = jwtService.generateToken(userCustomL);
        UserMapper user = userMapperMethod(userCustomL, phones);
        return new LoginResponse(user.getId(), new Date(System.currentTimeMillis()).toString(),
                new Date(System.currentTimeMillis()).toString(),
                token, true, user.getName(), user.getEmail(), user.getPassword(), user.getPhones());
    }

    private UserMapper userMapperMethod(UserCustom userCustomM, List<Phone> phones) {

        UserMapper user;
        List<PhoneMapper> phoneMappers = new ArrayList<>();
        try {
            phones.forEach(phone ->
                    phoneMappers.add(new PhoneMapper(phone.getNumber(), phone.getCitycode(), phone.getCountrycode())));
            user = UserMapper.builder().id(userCustomM.getId().toString()).name(userCustomM.getName()).email(userCustomM.getEmail())
                    .password(userCustomM.getPassword())
                    .phones(phoneMappers).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    private void validationParams(SignUpRequest registerRequest){
        String evaluation = this.validationsService.validationParams(registerRequest.getEmail(),
                registerRequest.getPassword());
        if (evaluation.contains("false")) {
            throw new IllegalArgumentException(" " + evaluation);
        }
    }

    private void validationParams(LoginRequest loginRequest){
        String evaluation = this.validationsService.validationParams(loginRequest.getEmail(),
                loginRequest.getPassword());
        if (evaluation.contains("false")) {
            throw new IllegalArgumentException(" " + evaluation);
        }
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