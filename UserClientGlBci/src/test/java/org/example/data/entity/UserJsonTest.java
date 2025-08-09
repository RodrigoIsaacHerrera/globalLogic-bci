package org.example.data.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

@JsonTest
public class UserJsonTest {

    @Autowired
    private JacksonTester<User> json;

    @Test
    void userSerializationTest() throws IOException {
        Resource resource = new ClassPathResource("JsonInputUser/expectedUser.json");
        User user = new User("Juan Pérez",
                "juan.perez@example.com",
                "password123");
        assertThat(json.write(user)).isNotEqualToJson(resource.getFile());
    }

    @Test
    void userSerializationStrictTest() throws IOException {
        Resource resource = new ClassPathResource("JsonInputUser/expectedUser.json");
        User user = new User("Juan Pérez",
                "juan.perez@example.com",
                "a2asfGfdfdf4");

        assertThat(json.write(user)).isEqualToJson(resource.getFile());
        assertThat(json.write(user)).hasJsonPathStringValue("@.id");
        assertThat(json.write(user)).extractingJsonPathStringValue("@.id").isNotEqualTo("784975a1-66ac-4357-a7b7-a1b8b13092e8");
        assertThat(json.write(user)).hasJsonPathStringValue("@.name");
        assertThat(json.write(user)).extractingJsonPathStringValue("@.name").isEqualTo("Juan Pérez");
        assertThat(json.write(user)).hasJsonPathStringValue("@.email");
        assertThat(json.write(user)).extractingJsonPathStringValue("@.email").isEqualTo("juan.perez@example.com");
        assertThat(json.write(user)).hasJsonPathStringValue("@.password");
        assertThat(json.write(user)).extractingJsonPathStringValue("@.password").isEqualTo("a2asfGfdfdf4");
    }
}
