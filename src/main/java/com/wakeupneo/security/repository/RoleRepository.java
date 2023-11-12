package com.wakeupneo.security.repository;

import com.wakeupneo.security.model.Permission;
import com.wakeupneo.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findRoleByName(String name);

}
