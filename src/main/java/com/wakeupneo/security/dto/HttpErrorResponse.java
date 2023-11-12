package com.wakeupneo.security.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpErrorResponse {

    private int statusCode;
    private HttpStatus status;
    private String reason;
    private String message;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    private Date timeStamp = new Date();

    public HttpErrorResponse(int statusCode, HttpStatus status, String reason, String message) {
        this.statusCode = statusCode;
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timeStamp = new Date();
    }
}
