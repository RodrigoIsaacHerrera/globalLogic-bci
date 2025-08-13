package org.example.data.mappers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PhoneMapper {

    Long number;
    Integer citycode;
    String countrycode;

}
