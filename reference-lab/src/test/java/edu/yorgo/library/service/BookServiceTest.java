package edu.yorgo.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.yorgo.library.domain.Book;
import edu.yorgo.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookService service;

    @Test
    void createClearsClientSuppliedIdBeforeSaving() {
        Book request = new Book("9780000000001", "Test Driven Development", "Kent Beck");
        request.setId(99L);
        when(repository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book created = service.create(request);

        assertThat(created.getId()).isNull();
        verify(repository).save(request);
    }
}
