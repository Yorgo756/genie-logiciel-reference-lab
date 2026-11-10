package edu.yorgo.library.service;

import edu.yorgo.library.domain.Book;
import edu.yorgo.library.repository.BookRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Book findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }

    public Book create(Book book) {
        book.setId(null);
        return repository.save(book);
    }

    public Book update(Long id, Book replacement) {
        Book book = findById(id);
        book.setIsbn(replacement.getIsbn());
        book.setTitle(replacement.getTitle());
        book.setAuthor(replacement.getAuthor());
        book.setAvailable(replacement.isAvailable());
        return repository.save(book);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
