package edu.yorgo.library.mapper;

import edu.yorgo.library.domain.Book;
import edu.yorgo.library.dto.BookRequest;
import edu.yorgo.library.dto.BookResponse;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(), book.isAvailable());
    }

    public Book toEntity(BookRequest request) {
        Book book = new Book(request.isbn(), request.title(), request.author());
        book.setAvailable(request.available() == null || request.available());
        return book;
    }
}
