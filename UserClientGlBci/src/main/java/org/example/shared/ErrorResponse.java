package org.example.shared;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int code;
    private String detail;

    public ErrorResponse(LocalDateTime timestamp, int code, String detail) {
        this.timestamp = timestamp;
        this.code = code;
        this.detail = detail;
    }


}
