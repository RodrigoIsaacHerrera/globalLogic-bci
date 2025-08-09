package org.example.data.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

@JsonTest
public class UserJsonTest {

    @Autowired
    private JacksonTester<User> json;

    @Test
    void userSerializationTest() throws IOException {

        User user = new User("Juan Pérez",
                "juan.perez@example.com",
                "password123");
        assertThat(json.write(user)).isStrictlyEqualToJson("expected.json");
    }
}
