package com.wakeupneo.security.service;

import com.wakeupneo.security.model.Permission;
import com.wakeupneo.security.model.Role;
import com.wakeupneo.security.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@AllArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public Role createRoleIfNotExists(String name, Collection<Permission> permissions) {

        Optional<Role> role = roleRepository.findRoleByName(name);
        if (role.isEmpty()) {
            Role newRole = new Role();
            newRole.setName(name);
            newRole.setPermissions(permissions);
            return roleRepository.save(newRole);
        }
        return role.get();
    }

    public Role getRole(String roleName) {
        return roleRepository.findRoleByName(roleName).orElseThrow(EntityNotFoundException::new);
    }

    public Collection<Role> getRoles(){
        return roleRepository.findAll();
    }
}
