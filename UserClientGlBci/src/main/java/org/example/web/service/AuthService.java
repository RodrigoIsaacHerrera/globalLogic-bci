package org.example.web.service;

import org.example.config.jwt.AccessRequest;
import org.example.config.jwt.AuthResponse;
import org.example.config.jwt.SignUpRequest;
import org.example.data.entity.Phone;
import org.example.data.entity.User;
import org.example.data.repository.PhonesRepository;
import org.example.data.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UsersRepository usersRepository;
    private final PhonesRepository phonesRepository;
    public AuthService(UsersRepository usersRepository, PhonesRepository phonesRepository){
        this.usersRepository = usersRepository;
        this.phonesRepository = phonesRepository;
    }
    public AuthResponse login(AccessRequest loginRequest) {
        return null;
    }

    public AuthResponse register(SignUpRequest registerRequest) {
        UUID userId;
        User signUser = new User(registerRequest);
        userId = signUser.getId();
        //aqui validar si existe y retornar error con su manejo de excepciones;
        usersRepository.save(signUser);
        registerRequest.getPhones().stream().forEach(phone -> {
            phonesRepository.save(new Phone(userId, phone.getNumber(), phone.getCitycode(), phone.getCountrycode()));
        });
        return AuthResponse.builder().token("creandoToken").build();
    }
}
