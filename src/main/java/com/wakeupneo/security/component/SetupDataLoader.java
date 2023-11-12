package com.wakeupneo.security.component;

import com.wakeupneo.security.model.Permission;
import com.wakeupneo.security.model.Role;
import com.wakeupneo.security.model.User;
import com.wakeupneo.security.service.PermissionService;
import com.wakeupneo.security.service.RoleService;
import com.wakeupneo.security.service.UserService;
import com.wakeupneo.security.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final UserService userService;

    public SetupDataLoader(RoleService roleService,
                           PermissionService permissionService,
                           UserServiceImpl userService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup) return;

        Permission createPermission = permissionService.createPermissionIfNotExists("permission:create");
        Permission readPermission = permissionService.createPermissionIfNotExists("permission:read");
        Permission updatePermission = permissionService.createPermissionIfNotExists("permission:update");
        Permission deletePermission = permissionService.createPermissionIfNotExists("permission:delete");

        Role adminRole = roleService.createRoleIfNotExists("ROLE_ADMIN",
                Arrays.asList(createPermission,
                        readPermission,
                        updatePermission,
                        deletePermission));
        Role userRole = roleService.createRoleIfNotExists("ROLE_USER",
                Collections.singletonList(readPermission));

        try {
            userService.registerUser(User.builder()
                    .name("admin")
                    .surname("test")
                    .username("testAdmin")
                    .password("test")
                    .email("admin@test.com")
                    .roles(Collections.singletonList(adminRole))
                    .enabled(true)
                    .build());

            userService.registerUser(User.builder()
                    .name("user")
                    .surname("test")
                    .username("testUser")
                    .password("test")
                    .email("user@test.com")
                    .roles(Collections.singletonList(userRole))
                    .enabled(true)
                    .build());
        } catch (Exception e) {
            log.info("initial users are already exists.");
        }
        alreadySetup = true;
    }

}
