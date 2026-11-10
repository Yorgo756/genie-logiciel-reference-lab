package edu.yorgo.library.mapper;

import edu.yorgo.library.domain.Reservation;
import edu.yorgo.library.dto.ReservationResponse;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    private final BookMapper bookMapper;
    private final MemberMapper memberMapper;

    public ReservationMapper(BookMapper bookMapper, MemberMapper memberMapper) {
        this.bookMapper = bookMapper;
        this.memberMapper = memberMapper;
    }

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                bookMapper.toResponse(reservation.getBook()),
                memberMapper.toResponse(reservation.getMember()),
                reservation.getReservedOn(),
                reservation.getCancelledOn());
    }
}
