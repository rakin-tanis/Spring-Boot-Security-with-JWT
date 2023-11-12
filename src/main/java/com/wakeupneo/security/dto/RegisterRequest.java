package com.wakeupneo.security.dto;

import com.wakeupneo.security.validator.Username;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.wakeupneo.security.constant.DtoConstants.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(max = MAX_LENGTH_EMAIL,
            min = MIN_LENGTH_USERNAME,
            message = "Username must be at least 4 characters length.")
    @Username
    private String username;
    @Email
    @NotBlank
    @Size(max = MAX_LENGTH_EMAIL)
    private String email;
    @NotBlank
    @Size(max = MAX_LENGTH_PASSWORD,
            min = MIN_LENGTH_PASSWORD,
            message = "Password must be at least 6 characters length.")
    private String password;

}
