package com.wakeupneo.security.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "support")
public class MailProp {

    private String email;

    private String applicationName;
}
