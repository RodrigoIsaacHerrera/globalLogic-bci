package org.example.data.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "phones", schema = "myapp")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    @Column(name = "number")
    private Long number;

    @Column(name = "citycode")
    private Integer citycode;

    @Column(name = "countrycode", length = 2)
    private String countrycode;

    public Phone() {}

    public Phone(UUID userId, Long number, Integer citycode, String countrycode) {
        this.userId = userId;
        this.number = number;
        this.citycode = citycode;
        this.countrycode = countrycode;
    }
}
