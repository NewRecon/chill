package ru.ivamly.chill.dto;

import jakarta.validation.constraints.NotNull;

public record AuthRq(
        @NotNull
        String username,
        @NotNull
        String password
) {
}
