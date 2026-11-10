package edu.yorgo.library.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BorrowingRequest(
        @NotNull Long bookId,
        @NotNull Long memberId,
        @NotNull LocalDate borrowedOn,
        LocalDate dueDate,
        LocalDate returnedOn) {
}
