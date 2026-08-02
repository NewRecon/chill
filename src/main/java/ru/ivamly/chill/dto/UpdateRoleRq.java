package ru.ivamly.chill.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import ru.ivamly.chill.entity.enums.Role;

public record UpdateRoleRq(
        @NotNull
        UUID userId,
        @NotNull
        Role role
) {

}
