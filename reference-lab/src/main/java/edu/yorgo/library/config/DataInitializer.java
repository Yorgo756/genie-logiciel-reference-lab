package edu.yorgo.library.config;

import edu.yorgo.library.domain.Book;
import edu.yorgo.library.domain.Member;
import edu.yorgo.library.repository.BookRepository;
import edu.yorgo.library.repository.MemberRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedLibrary(BookRepository books, MemberRepository members) {
        return args -> {
            if (books.count() == 0) {
                books.saveAll(List.of(
                        new Book("9780132350884", "Clean Code", "Robert C. Martin"),
                        new Book("9780201633610", "Design Patterns", "Erich Gamma et al."),
                        new Book("9780134685991", "Effective Java", "Joshua Bloch"),
                        new Book("9781617294945", "Spring in Action", "Craig Walls"),
                        new Book("9781491950357", "Building Microservices", "Sam Newman")));
            }
            if (members.count() == 0) {
                members.saveAll(List.of(
                        new Member("Alice Martin", "alice@example.com"),
                        new Member("Bruno Haddad", "bruno@example.com"),
                        new Member("Carla Nassar", "carla@example.com")));
            }
        };
    }
}
