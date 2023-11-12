package com.wakeupneo.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakeupneo.security.dto.HttpErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        HttpErrorResponse httpResponse = HttpErrorResponse.builder()
                .statusCode(UNAUTHORIZED.value())
                .status(UNAUTHORIZED)
                .reason(UNAUTHORIZED.getReasonPhrase().toUpperCase())
                .message(accessDeniedException.getMessage())
                .build();

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());

        OutputStream outputStream = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream, httpResponse);
        outputStream.flush();

    }
}
