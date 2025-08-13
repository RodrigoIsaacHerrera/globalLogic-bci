package org.example.web.request;

import lombok.*;
import org.example.data.entity.Phone;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest extends LoginRequest {
    String name;
    List<Phone> phones;
}
