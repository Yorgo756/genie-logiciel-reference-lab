package edu.yorgo.library.web;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BorrowingRequest(
        @NotNull Long bookId,
        @NotNull Long memberId,
        @NotNull LocalDate borrowedOn,
        @NotNull LocalDate dueDate,
        LocalDate returnedOn) {
}
