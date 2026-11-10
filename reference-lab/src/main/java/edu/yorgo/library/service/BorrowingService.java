package edu.yorgo.library.service;

import edu.yorgo.library.domain.Borrowing;
import edu.yorgo.library.repository.BorrowingRepository;
import edu.yorgo.library.web.BorrowingRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
        Borrowing borrowing = new Borrowing();
        applyRequest(borrowing, request);
        return repository.save(borrowing);
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
}
