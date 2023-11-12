package com.wakeupneo.security.config;

import com.wakeupneo.security.config.prop.JwtProp;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@RequiredArgsConstructor
@Configuration
public class SecretKeyConfig {

    private final JwtProp jwtProp;

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtProp.getSecretKey().getBytes());
    }
}
