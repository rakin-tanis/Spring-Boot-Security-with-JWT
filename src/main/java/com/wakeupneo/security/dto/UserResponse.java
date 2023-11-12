package com.wakeupneo.security.dto;

import com.wakeupneo.security.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String email;
    private String username;
    private String name;
    private String surname;
    private Set<RoleResponse> roles;
    private String profileImageUrl;
    private Date lastLoginDateDisplay;
    private boolean verified;
    private UserStatus status;
    private boolean accountExpired;
    private boolean accountLocked;
    private boolean credentialsExpired;
    private boolean enabled;


}
