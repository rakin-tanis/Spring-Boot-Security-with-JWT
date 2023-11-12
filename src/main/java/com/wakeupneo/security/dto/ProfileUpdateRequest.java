package com.wakeupneo.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.wakeupneo.security.constant.DtoConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest {

    @Email
    @Size(max = MAX_LENGTH_EMAIL)
    private String email;
    @Size(max = MAX_LENGTH_NAME)
    private String name;
    @Size(max = MAX_LENGTH_SURNAME)
    private String surname;

}
