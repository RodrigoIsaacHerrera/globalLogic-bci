package org.example.data.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.UUID;

@JsonTest
public class UserCustomJsonTest {

    @Autowired
    private JacksonTester<UserCustom> json;

    @Test
    void userSerializationTest() throws IOException {
        Resource resource = new ClassPathResource("JsonInputUser/expectedUser.json");
        UUID uuid = UUID.randomUUID();
        UserCustom userCustom = new UserCustom(uuid, "Juan Pérez", "juan.perez@example.com", "password123");
        assertThat(json.write(userCustom)).isNotEqualToJson(resource.getFile());
    }

    @Test
    void userSerializationStrictTest() throws IOException {
        Resource resource = new ClassPathResource("JsonInputUser/expectedUser.json");
        UUID uuid = UUID.randomUUID();
        UserCustom userCustom = new UserCustom(uuid,"Juan Pérez",
                "juan.perez@example.com",
                "a2asfGfdfdf4");

        assertThat(json.write(userCustom)).isEqualToJson(resource.getFile());
        assertThat(json.write(userCustom)).hasJsonPathStringValue("@.id");
        assertThat(json.write(userCustom)).extractingJsonPathStringValue("@.id").isNotEqualTo("784975a1-66ac-4357-a7b7-a1b8b13092e8");
        assertThat(json.write(userCustom)).hasJsonPathStringValue("@.name");
        assertThat(json.write(userCustom)).extractingJsonPathStringValue("@.name").isEqualTo("Juan Pérez");
        assertThat(json.write(userCustom)).hasJsonPathStringValue("@.email");
        assertThat(json.write(userCustom)).extractingJsonPathStringValue("@.email").isEqualTo("juan.perez@example.com");
        assertThat(json.write(userCustom)).hasJsonPathStringValue("@.password");
        assertThat(json.write(userCustom)).extractingJsonPathStringValue("@.password").isEqualTo("a2asfGfdfdf4");
    }
}
