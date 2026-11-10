package edu.yorgo.library.web;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationRequest(@NotNull Long bookId, @NotNull LocalDate reservedOn) {
}
