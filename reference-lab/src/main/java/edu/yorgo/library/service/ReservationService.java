package edu.yorgo.library.service;

import edu.yorgo.library.domain.Reservation;
import edu.yorgo.library.repository.ReservationRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservationService {

    private final ReservationRepository repository;
    private final BookService bookService;
    private final MemberService memberService;

    public ReservationService(
            ReservationRepository repository,
            BookService bookService,
            MemberService memberService) {
        this.repository = repository;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @Transactional
    public Reservation create(Long memberId, Long bookId, LocalDate reservedOn) {
        var member = memberService.findById(memberId);
        var book = bookService.findById(bookId);
        if (!member.isActive()) {
            throw conflict("Member is not active");
        }
        if (book.isAvailable()) {
            throw conflict("Available books must be borrowed directly");
        }
        if (repository.existsByMemberIdAndBookIdAndCancelledOnIsNull(memberId, bookId)) {
            throw conflict("Member already has an active reservation for this book");
        }
        return repository.save(new Reservation(book, member, reservedOn));
    }

    public List<Reservation> findActiveByMember(Long memberId) {
        memberService.findById(memberId);
        return repository.findByMemberIdAndCancelledOnIsNullOrderByReservedOnAsc(memberId);
    }

    @Transactional
    public void cancel(Long memberId, Long reservationId) {
        memberService.findById(memberId);
        Reservation reservation = repository.findByIdAndMemberIdAndCancelledOnIsNull(reservationId, memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
        reservation.setCancelledOn(LocalDate.now());
        repository.save(reservation);
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
