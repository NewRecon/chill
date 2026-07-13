package ru.ivamly.chill.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import ru.ivamly.chill.entity.enums.ChillType;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateChillRq(
        @NotNull
        UUID userId, // TODO это поле будет браться из LDAP?
        @NotNull
        ChillType type,
        String comment,
        @FutureOrPresent
        LocalDate startDate,
        LocalDate endDate
) {
    @AssertTrue
    public boolean isStartBeforeOrEqualEnd() {
        return startDate != null && endDate != null && !startDate.isAfter(endDate);
    }

    @AssertTrue // TODO для этой проверки должна появится аннотация в hibernate-validator 9.2
    public boolean isCommentNullOrNotBlank() {
        return comment == null || !comment.isBlank();
    }
}
