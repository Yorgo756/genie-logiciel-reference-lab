package edu.yorgo.library.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.yorgo.library.repository.BookRepository;
import edu.yorgo.library.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LibraryHttpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository books;

    @Autowired
    private MemberRepository members;

    @Test
    void listsSeededBooksAndMembers() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void createsUpdatesAndDeletesBook() throws Exception {
        String response = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "isbn": "9780321146533",
                                  "title": "Test Driven Development",
                                  "author": "Kent Beck",
                                  "available": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("Test Driven Development"))
                .andReturn().getResponse().getContentAsString();

        Number parsedId = com.jayway.jsonpath.JsonPath.read(response, "$.id");
        long id = parsedId.longValue();

        mockMvc.perform(put("/api/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "isbn": "9780321146533",
                                  "title": "TDD by Example",
                                  "author": "Kent Beck",
                                  "available": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("TDD by Example"));

        mockMvc.perform(delete("/api/books/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void createsBorrowingFromExistingBookAndMember() throws Exception {
        Long bookId = books.findAll().get(0).getId();
        Long memberId = members.findAll().get(0).getId();

        mockMvc.perform(post("/api/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookId": %d,
                                  "memberId": %d,
                                  "borrowedOn": "2026-11-10",
                                  "dueDate": "2026-11-24"
                                }
                                """.formatted(bookId, memberId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.book.id").value(bookId))
                .andExpect(jsonPath("$.member.id").value(memberId));
    }

    @Test
    void rejectsInvalidBook() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\":\"\",\"title\":\"\",\"author\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publishesOpenApiAndSwaggerUi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Library Management API"))
                .andExpect(jsonPath("$.paths['/api/books']").exists());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
