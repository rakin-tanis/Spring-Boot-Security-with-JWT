package com.wakeupneo.security.service;

import com.wakeupneo.security.model.Permission;
import com.wakeupneo.security.repository.PermissionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional
    public Permission createPermissionIfNotExists(String name) {

        Optional<Permission> permission = permissionRepository.findPermissionByName(name);
        if (permission.isEmpty()) {
            Permission newPermission = new Permission();
            newPermission.setName(name);
            return permissionRepository.save(newPermission);
        }
        return permission.get();
    }

    public Collection<String> getPermissions() {
        return permissionRepository.findAll()
                .stream()
                .map(Permission::getName)
                .map(name -> name.replace("permission:", ""))
                .collect(Collectors.toList());
    }

}
