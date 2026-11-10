package edu.yorgo.library.web;

import edu.yorgo.library.domain.Borrowing;
import edu.yorgo.library.service.BorrowingService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members/{memberId}/borrowings")
public class MemberBorrowingController {

    private final BorrowingService service;

    public MemberBorrowingController(BorrowingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Borrowing> borrow(
            @PathVariable Long memberId,
            @Valid @RequestBody BorrowBookRequest request) {
        Borrowing created = service.borrowBook(memberId, request.bookId(), request.borrowedOn());
        return ResponseEntity.created(URI.create("/api/borrowings/" + created.getId())).body(created);
    }

    @GetMapping
    public List<Borrowing> list(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "true") boolean active) {
        if (!active) {
            throw new IllegalArgumentException("Only active borrowing lists are supported");
        }
        return service.findActiveByMember(memberId);
    }
}
