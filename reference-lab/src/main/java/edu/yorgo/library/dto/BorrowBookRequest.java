package edu.yorgo.library.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BorrowBookRequest(@NotNull Long bookId, @NotNull LocalDate borrowedOn) {
}
