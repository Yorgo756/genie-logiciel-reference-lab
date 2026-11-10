package edu.yorgo.library.web;

import edu.yorgo.library.domain.Reservation;
import edu.yorgo.library.service.ReservationService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members/{memberId}/reservations")
public class MemberReservationController {

    private final ReservationService service;

    public MemberReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Reservation> create(
            @PathVariable Long memberId,
            @Valid @RequestBody ReservationRequest request) {
        Reservation created = service.create(memberId, request.bookId(), request.reservedOn());
        URI location = URI.create("/api/members/" + memberId + "/reservations/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<Reservation> list(@PathVariable Long memberId) {
        return service.findActiveByMember(memberId);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long memberId,
            @PathVariable Long reservationId) {
        service.cancel(memberId, reservationId);
        return ResponseEntity.noContent().build();
    }
}
