package org.example.config.jwt;

import lombok.*;
import org.example.data.entity.Phone;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    String name, email,  password;
    List<Phone> phones;
}
