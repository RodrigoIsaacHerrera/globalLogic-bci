package org.example.config.jwt;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessRequest {
    String email, password;
}
