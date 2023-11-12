package com.wakeupneo.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.wakeupneo.security.constant.DtoConstants.MAX_LENGTH_EMAIL;
import static com.wakeupneo.security.constant.DtoConstants.MAX_LENGTH_PASSWORD;

@Data
@AllArgsConstructor
public class EmailLoginRequest {

    @Email
    @NotBlank
    @Size(max = MAX_LENGTH_EMAIL)
    private String email;

    @NotBlank
    @Size(max = MAX_LENGTH_PASSWORD)
    private String password;

}
