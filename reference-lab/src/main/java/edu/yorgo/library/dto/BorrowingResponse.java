package edu.yorgo.library.dto;

import java.time.LocalDate;

public record BorrowingResponse(
        Long id,
        BookResponse book,
        MemberResponse member,
        LocalDate borrowedOn,
        LocalDate dueDate,
        LocalDate returnedOn) {
}
