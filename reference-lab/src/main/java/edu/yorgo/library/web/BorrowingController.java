package edu.yorgo.library.web;

import edu.yorgo.library.domain.Borrowing;
import edu.yorgo.library.dto.BorrowingRequest;
import edu.yorgo.library.dto.BorrowingResponse;
import edu.yorgo.library.dto.LateFeeResponse;
import edu.yorgo.library.dto.ReturnBookRequest;
import edu.yorgo.library.mapper.BorrowingMapper;
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
    private final BorrowingMapper mapper;

    public BorrowingController(BorrowingService service, BorrowingMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<BorrowingResponse> findAll() {
        return service.findAll().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public BorrowingResponse findById(@PathVariable Long id) {
        return mapper.toResponse(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<BorrowingResponse> create(@Valid @RequestBody BorrowingRequest request) {
        Borrowing created = service.create(request);
        return ResponseEntity.created(URI.create("/api/borrowings/" + created.getId())).body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public BorrowingResponse update(@PathVariable Long id, @Valid @RequestBody BorrowingRequest request) {
        return mapper.toResponse(service.update(id, request));
    }

    @PutMapping("/{id}/return")
    public BorrowingResponse returnBook(@PathVariable Long id, @Valid @RequestBody ReturnBookRequest request) {
        return mapper.toResponse(service.returnBook(id, request.returnedOn()));
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
