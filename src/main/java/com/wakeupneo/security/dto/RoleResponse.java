package com.wakeupneo.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private String name;
    private Collection<String> permissions;

}
