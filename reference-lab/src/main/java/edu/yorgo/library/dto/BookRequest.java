package edu.yorgo.library.dto;

import jakarta.validation.constraints.NotBlank;

public record BookRequest(
        @NotBlank String isbn,
        @NotBlank String title,
        @NotBlank String author,
        Boolean available) {
}
