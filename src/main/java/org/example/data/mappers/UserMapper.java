package org.example.data.mappers;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UserMapper {

    String id;
    String name;
    String email;
    String password;
    List<PhoneMapper> phones;

}
