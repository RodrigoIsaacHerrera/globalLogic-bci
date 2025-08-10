package org.example.web.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.data.entity.Phone;
import org.example.data.entity.User;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse{

    private String id, created, lastLogin, token;
    private boolean isActive;
    private String name;
    private String email;
    private String password;
    private List<Phone> phones;

    public LoginResponse(SignUpResponse signUpResponse, List<Phone> phones, User user) {
        this.id = signUpResponse.getId();
        this.created = signUpResponse.getCreated();
        this.lastLogin = signUpResponse.getLastLogin();
        this.token = signUpResponse.getToken();
        this.isActive = signUpResponse.isActive();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.phones = phones;
    }

}
