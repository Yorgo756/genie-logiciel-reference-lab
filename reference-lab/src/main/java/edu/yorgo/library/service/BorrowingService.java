package edu.yorgo.library.service;

import edu.yorgo.library.domain.Borrowing;
import edu.yorgo.library.repository.BorrowingRepository;
import edu.yorgo.library.web.BorrowingRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BorrowingService {

    private final BorrowingRepository repository;
    private final BookService bookService;
    private final MemberService memberService;

    public BorrowingService(
            BorrowingRepository repository,
            BookService bookService,
            MemberService memberService) {
        this.repository = repository;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    public List<Borrowing> findAll() {
        return repository.findAll();
    }

    public Borrowing findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrowing not found"));
    }

    public Borrowing create(BorrowingRequest request) {
        return borrowBook(request.memberId(), request.bookId(), request.borrowedOn());
    }

    public Borrowing update(Long id, BorrowingRequest request) {
        Borrowing borrowing = findById(id);
        applyRequest(borrowing, request);
        return repository.save(borrowing);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void applyRequest(Borrowing borrowing, BorrowingRequest request) {
        borrowing.setBook(bookService.findById(request.bookId()));
        borrowing.setMember(memberService.findById(request.memberId()));
        borrowing.setBorrowedOn(request.borrowedOn());
        borrowing.setDueDate(request.dueDate());
        borrowing.setReturnedOn(request.returnedOn());
    }

    @Transactional
    public Borrowing borrowBook(Long memberId, Long bookId, LocalDate borrowedOn) {
        var member = memberService.findById(memberId);
        var book = bookService.findById(bookId);
        if (!member.isActive()) {
            throw conflict("Member is not active");
        }
        if (!book.isAvailable() || repository.existsByBookIdAndReturnedOnIsNull(bookId)) {
            throw conflict("Book already has an active borrowing");
        }

        book.setAvailable(false);
        Borrowing borrowing = new Borrowing(book, member, borrowedOn, borrowedOn.plusDays(14));
        return repository.save(borrowing);
    }

    @Transactional
    public Borrowing returnBook(Long borrowingId, LocalDate returnedOn) {
        Borrowing borrowing = findById(borrowingId);
        if (borrowing.getReturnedOn() != null) {
            throw conflict("Borrowing has already been returned");
        }
        if (returnedOn.isBefore(borrowing.getBorrowedOn())) {
            throw conflict("Return date cannot precede borrowing date");
        }
        borrowing.setReturnedOn(returnedOn);
        borrowing.getBook().setAvailable(true);
        return repository.save(borrowing);
    }

    public List<Borrowing> findActiveByMember(Long memberId) {
        memberService.findById(memberId);
        return repository.findByMemberIdAndReturnedOnIsNullOrderByBorrowedOnAsc(memberId);
    }

    public LateFee calculateLateFee(Long borrowingId, LocalDate asOf) {
        Borrowing borrowing = findById(borrowingId);
        LocalDate chargeThrough = borrowing.getReturnedOn() == null ? asOf : borrowing.getReturnedOn();
        long lateDays = Math.max(0, ChronoUnit.DAYS.between(borrowing.getDueDate(), chargeThrough));
        BigDecimal amount = BigDecimal.valueOf(lateDays)
                .multiply(new BigDecimal("0.50"))
                .setScale(2);
        return new LateFee(lateDays, amount);
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
