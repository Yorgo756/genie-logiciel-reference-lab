package edu.yorgo.library.dto;

import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        BookResponse book,
        MemberResponse member,
        LocalDate reservedOn,
        LocalDate cancelledOn) {
}
