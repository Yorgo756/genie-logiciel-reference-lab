package edu.yorgo.library;

import static org.assertj.core.api.Assertions.assertThat;

import edu.yorgo.library.repository.BookRepository;
import edu.yorgo.library.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SeedDataIntegrationTest {

    @Autowired
    private BookRepository books;

    @Autowired
    private MemberRepository members;

    @Test
    void seedsFiveBooksAndThreeMembers() {
        assertThat(books.count()).isEqualTo(5);
        assertThat(members.count()).isEqualTo(3);
    }
}
