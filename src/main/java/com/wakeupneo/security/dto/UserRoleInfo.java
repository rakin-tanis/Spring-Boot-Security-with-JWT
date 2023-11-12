package com.wakeupneo.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
public record UserRoleInfo(Long userId, long roles, Long lastLoginTimestamp) {
}
