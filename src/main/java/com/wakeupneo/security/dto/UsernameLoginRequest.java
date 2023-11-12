package com.wakeupneo.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.wakeupneo.security.constant.DtoConstants.MAX_LENGTH_PASSWORD;
import static com.wakeupneo.security.constant.DtoConstants.MAX_LENGTH_USERNAME;

@Data
@AllArgsConstructor
public class UsernameLoginRequest {

    @NotBlank
    @Size(max = MAX_LENGTH_USERNAME)
    private String username;

    @NotBlank
    @Size(max = MAX_LENGTH_PASSWORD)
    private String password;

}
