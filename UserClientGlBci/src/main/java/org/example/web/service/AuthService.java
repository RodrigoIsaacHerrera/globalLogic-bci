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

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UsersRepository usersRepository;
    private final PhonesRepository phonesRepository;
    private final JwtService jwtService;

    public AuthService(UsersRepository usersRepository, PhonesRepository phonesRepository, JwtService jwtService){
        this.usersRepository = usersRepository;
        this.phonesRepository = phonesRepository;
        this.jwtService = jwtService;
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
        //cambiar aqui jwtService.generateToken()
        // en caso de save exitoso del usuario (opcional telefono) instanciar objeto RegisteredUser
        // pendiente modificar el objeto AuthResponse (completar) de respuesta a HTTPie app

        return AuthResponse.builder()
                .token(jwtService.generateRecordToken(usersRepository.findByEmailContainingIgnoreCase(signUser.getEmail())
                        .get())).build();
    }
    static void assemblerObjectRecord(User userR, List<Phone> phoneList){
        /*
        refactorizar este metodo para retornar al FrontEnd, esta clase ayudara heredando sus atributos
        a el objeto json a retornar de endpoint login.

        RegisteredUser

        id: id del usuario (puede ser lo que se genera por el banco de datos, pero sería más deseable un UUID)

        crear tabla con su servicio RECFL (RECORD FORWARD LOG)
        ○ created: fecha de creación del usuario
        ○ lastLogin: del último ingreso
        ○ token: token de acceso de la API (debe utilizar JWT)
        ○ isActive: Indica si el usuario sigue habilitado dentro del sistema.*/
    }
}
