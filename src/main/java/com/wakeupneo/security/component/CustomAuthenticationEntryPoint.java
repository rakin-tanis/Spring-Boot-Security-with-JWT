package com.wakeupneo.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakeupneo.security.dto.HttpErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class CustomAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {

        HttpErrorResponse httpResponse = HttpErrorResponse.builder()
                .statusCode(UNAUTHORIZED.value())
                .status(UNAUTHORIZED)
                .reason(UNAUTHORIZED.getReasonPhrase().toUpperCase())
                .message(authenticationException.getMessage())
                .build();

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());

        OutputStream outputStream = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
