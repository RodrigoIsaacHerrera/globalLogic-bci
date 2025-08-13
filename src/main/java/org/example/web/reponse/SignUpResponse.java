package org.example.web.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.data.mappers.UserMapper;


@Getter
@Setter
@AllArgsConstructor
public class SignUpResponse {
    UserMapper user;
    String id;
    String created;
    String lastLogin;
    String token;
    boolean isActive;
}
