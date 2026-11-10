package edu.yorgo.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberRequest(@NotBlank String name, @Email @NotBlank String email, Boolean active) {
}
