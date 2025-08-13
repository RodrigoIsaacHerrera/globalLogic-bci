package org.example.web.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.data.entity.Phone;
import org.example.data.mappers.PhoneMapper;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private String id;
    private String created;
    private String lastLogin;
    private String token;
    private boolean isActive;
    private String name;
    private String email;
    private String password;
    private List<PhoneMapper> phones;

}
