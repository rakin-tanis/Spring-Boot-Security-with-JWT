package com.wakeupneo.security.config;

import com.wakeupneo.security.dto.RoleResponse;
import com.wakeupneo.security.model.Permission;
import com.wakeupneo.security.model.Role;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper getMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.createTypeMap(Role.class, RoleResponse.class).setPostConverter(
                context -> {
                    Collection<Permission> permissions = context.getSource().getPermissions();
                    context.getDestination().setPermissions(permissions.stream()
                            .map(p -> p.getName().replace("permission:", ""))
                            .collect(Collectors.toList()));
                    context.getDestination()
                            .setName(context.getSource().getName().replace("ROLE_", ""));
                    return context.getDestination();
                }
        );
        return mapper;
    }

}
