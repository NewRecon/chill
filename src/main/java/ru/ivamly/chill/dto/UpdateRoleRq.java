package ru.ivamly.chill.dto;

import java.util.UUID;

import ru.ivamly.chill.entity.enums.Role;

public record UpdateRoleRq(
        UUID userId,
        Role role
) {

}
