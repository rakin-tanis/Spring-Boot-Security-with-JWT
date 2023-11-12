package com.wakeupneo.security.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;

@Data
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProp {

    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpirationAfterDays;
    private String issuer;
    private String audience;

    public String getAuthorizationHeader(){
        return HttpHeaders.AUTHORIZATION;
    }

}
