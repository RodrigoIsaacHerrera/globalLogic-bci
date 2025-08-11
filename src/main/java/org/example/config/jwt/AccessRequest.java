package org.example.config.jwt;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessRequest {
    String email;
    String password;
}
