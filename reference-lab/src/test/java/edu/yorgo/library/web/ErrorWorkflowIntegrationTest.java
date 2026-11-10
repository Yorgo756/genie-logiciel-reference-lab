package edu.yorgo.library.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
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
class ErrorWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository books;

    @Autowired
    private MemberRepository members;

    @Test
    void returnsConsistentErrorShapeForMissingBook() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 99999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Book not found"))
                .andExpect(jsonPath("$.path").value("/api/books/99999"));
    }

    @Test
    void reportsInvalidMemberFieldsAsBadRequest() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("email")))
                .andExpect(jsonPath("$.message", containsString("name")))
                .andExpect(jsonPath("$.path").value("/api/members"));
    }

    @Test
    void rejectsMissingMemberAndBookDuringBorrowing() throws Exception {
        long bookId = books.findAll().get(0).getId();
        long memberId = members.findAll().get(0).getId();

        mockMvc.perform(post("/api/members/{memberId}/borrowings", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowRequest(bookId, "2026-11-10")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Member not found"));

        mockMvc.perform(post("/api/members/{memberId}/borrowings", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowRequest(99999, "2026-11-10")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void completedLoanFeeStopsAtReturnDate() throws Exception {
        long bookId = books.findAll().get(1).getId();
        long memberId = members.findAll().get(1).getId();
        String response = mockMvc.perform(post("/api/members/{memberId}/borrowings", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowRequest(bookId, "2026-10-01")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Number borrowingId = JsonPath.read(response, "$.id");

        mockMvc.perform(put("/api/borrowings/{id}/return", borrowingId.longValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"returnedOn\":\"2026-10-17\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/borrowings/{id}/late-fee", borrowingId.longValue())
                        .queryParam("asOf", "2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lateDays").value(2))
                .andExpect(jsonPath("$.amount").value(1.00));
    }

    private String borrowRequest(long bookId, String borrowedOn) {
        return "{\"bookId\":" + bookId + ",\"borrowedOn\":\"" + borrowedOn + "\"}";
    }
}
