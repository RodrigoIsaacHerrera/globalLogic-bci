package org.example.shared;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GenericMessageApp implements Serializable {

    private String message;

    public GenericMessageApp(String message) {
        this.message = message;
    }
}
