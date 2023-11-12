package com.wakeupneo.security.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "login-attempt-cache")
public class LoginAttemptCacheProp {

    private int incorrectAttemptCount;
    private int durationInMinutes;

}
