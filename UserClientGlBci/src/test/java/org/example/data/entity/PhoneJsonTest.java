package org.example.data.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneJsonTest {

    @Autowired
    private JacksonTester<Phone> json;

    @Test
    void phoneSerializationTest() throws IOException {
        Resource resource = new ClassPathResource("JsonInputPhone/expectedPhone.json");
        Phone phone = new Phone(87650009L,  7,  "25");
        assertThat(json.write(phone)).isNotEqualToJson(resource.getFile());
        assertThat(json.write(phone)).hasJsonPathStringValue("@.id");
    }

    @Test
    void phoneSerializationStrictTest() throws IOException {
        Resource resource = new ClassPathResource("JsonInputPhone/expectedPhone.json");
        Phone phone = new Phone(87650009L,  7,  "25");

        assertThat(json.write(phone)).isEqualToJson(resource.getFile());
        assertThat(json.write(phone)).hasJsonPathStringValue("@.name");
        assertThat(json.write(phone)).extractingJsonPathNumberValue("@.name").isEqualTo("Juan Pérez");
        assertThat(json.write(phone)).hasJsonPathStringValue("@.email");
        assertThat(json.write(phone)).extractingJsonPathStringValue("@.email").isEqualTo("juan.perez@example.com");
        assertThat(json.write(phone)).hasJsonPathStringValue("@.password");
        assertThat(json.write(phone)).extractingJsonPathStringValue("@.password").isEqualTo("a2asfGfdfdf4");
    }

}
