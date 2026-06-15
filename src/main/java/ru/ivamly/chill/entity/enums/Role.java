package ru.ivamly.chill.entity.enums;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_MANAGER,
    ROLE_ADMIN;

    @Override
    public @Nullable String getAuthority() {
        return name();
    }
}
