package edu.yorgo.library.web;

import edu.yorgo.library.domain.Borrowing;
import edu.yorgo.library.service.BorrowingService;
import edu.yorgo.library.service.LateFee;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    private final BorrowingService service;

    public BorrowingController(BorrowingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Borrowing> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Borrowing findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Borrowing> create(@Valid @RequestBody BorrowingRequest request) {
        Borrowing created = service.create(request);
        return ResponseEntity.created(URI.create("/api/borrowings/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public Borrowing update(@PathVariable Long id, @Valid @RequestBody BorrowingRequest request) {
        return service.update(id, request);
    }

    @PutMapping("/{id}/return")
    public Borrowing returnBook(@PathVariable Long id, @Valid @RequestBody ReturnBookRequest request) {
        return service.returnBook(id, request.returnedOn());
    }

    @GetMapping("/{id}/late-fee")
    public LateFeeResponse lateFee(@PathVariable Long id, @RequestParam LocalDate asOf) {
        LateFee fee = service.calculateLateFee(id, asOf);
        return new LateFeeResponse("EUR", fee.lateDays(), fee.amount());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
