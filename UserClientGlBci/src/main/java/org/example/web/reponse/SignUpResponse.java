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
public class SignUpResponse implements UserDetails {
    User user;
    String id, created, lastLogin, token, password;
    boolean isActive;


    public SignUpResponse(String id, String created, String lastLogin, String token, String password, boolean isActive) {
        this.id = id;
        this.created = created;
        this.lastLogin = lastLogin;
        this.token = token;
        this.password = password;
        this.isActive = isActive;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
