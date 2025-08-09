package org.example.shared;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {

    public ErrorResponse(String message, int code, String status, LocalDateTime timestamp) {
        this.message = message;
        this.code = code;
        this.status = status;
        this.timestamp = timestamp;
    }

    private String message;
    private int code;
    private String status;
    private LocalDateTime timestamp;

}
