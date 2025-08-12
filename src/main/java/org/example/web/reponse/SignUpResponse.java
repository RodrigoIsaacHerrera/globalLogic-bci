package org.example.web.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.data.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class SignUpResponse {
    User user;
    String id, created, lastLogin, token, password;
    boolean isActive;
}
