package edu.yorgo.library.web;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReturnBookRequest(@NotNull LocalDate returnedOn) {
}
