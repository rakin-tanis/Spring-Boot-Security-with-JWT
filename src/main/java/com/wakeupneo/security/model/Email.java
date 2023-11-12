package com.wakeupneo.security.model;

import lombok.*;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Email {

    String to;
    String from;
    String subject;
    String templateName;
    private Map<String, Object> model;

}
