package com.wakeupneo.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.wakeupneo.security.constant.DtoConstants.MAX_LENGTH_PASSWORD;
import static com.wakeupneo.security.constant.DtoConstants.MIN_LENGTH_PASSWORD;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLoggedInPasswordRequest {

    @NotBlank
    @Size(max = MAX_LENGTH_PASSWORD,
            min = MIN_LENGTH_PASSWORD,
            message = "Password must be at least 6 characters length.")
    private String oldPassword;
    @NotBlank
    @Size(max = MAX_LENGTH_PASSWORD,
            min = MIN_LENGTH_PASSWORD,
            message = "Password must be at least 6 characters length.")
    private String newPassword;
}
