package org.example.data.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class PhoneJsonTest {


    @Autowired
    private JacksonTester<Phone> json;

    private User user;

    @BeforeEach
    void setUp() {
        UUID uuid = UUID.randomUUID();
        user = new User(uuid,"Carlos Ruiz","carlos@example.com","pass123");
    }

    @Test
    void testSerializacionToJson() throws Exception {

        Resource resource = new ClassPathResource("JsonInputPhone/expectedPhone.json");
        Phone phone = new Phone(user.getId(),87650009L,  7,  "25");
        phone.setId(1L);

        assertThat(json.write(phone)).isEqualToJson(resource.getFile());
        assertThat(json.write(phone)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(phone)).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(json.write(phone)).extractingJsonPathNumberValue("@.number").isEqualTo(87650009);
        assertThat(json.write(phone)).extractingJsonPathNumberValue("@.citycode").isEqualTo(7);
        assertThat(json.write(phone)).extractingJsonPathStringValue("@.countrycode").isEqualTo("25");

        System.out.println("JSON generado:\n" + json.write(phone));
    }


    @Test
    void phoneSerializationTest() throws IOException {
        Resource resource = new ClassPathResource("JsonInputPhone/expectedPhone.json");
        Phone phone = new Phone(user.getId(),96650009L,  7,  "25");
        phone.setId(1L);
        assertThat(json.write(phone)).isNotEqualToJson(resource.getFile());
        assertThat(json.write(phone)).hasJsonPathNumberValue("@.id");

        System.out.println("JSON generado:\n" + json.write(phone));
    }

    @Test
    void phoneSerializationStrictTest() throws IOException {
        Resource resource = new ClassPathResource("JsonInputPhone/expectedPhone.json");
        Phone phone = new Phone(user.getId(),null,  null,  null);
        phone.setId(1L);

        assertThat(json.write(phone)).isNotEqualToJson(resource.getFile());
        assertThat(json.write(phone)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(phone)).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(json.write(phone)).hasJsonPathValue("@.userId");
        assertThat(json.write(phone)).hasEmptyJsonPathValue("@.number");
        assertThat(json.write(phone)).hasEmptyJsonPathValue("@.citycode");
        assertThat(json.write(phone)).hasEmptyJsonPathValue("@.countrycode");

        System.out.println("JSON generado:\n" + json.write(phone));
    }
}
