package edu.yorgo.library.web;

import static org.hamcrest.Matchers.hasSize;
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
class BorrowingWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository books;

    @Autowired
    private MemberRepository members;

    @Test
    void borrowsForFourteenDaysAndRejectsDuplicateActiveBorrowing() throws Exception {
        long bookId = books.findAll().get(0).getId();
        long memberId = members.findAll().get(0).getId();
        String request = borrowRequest(bookId, "2026-11-10");

        mockMvc.perform(post("/api/members/{memberId}/borrowings", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.borrowedOn").value("2026-11-10"))
                .andExpect(jsonPath("$.dueDate").value("2026-11-24"))
                .andExpect(jsonPath("$.book.available").value(false));

        mockMvc.perform(post("/api/members/{memberId}/borrowings", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict());
    }

    @Test
    void returnsLoanRestoresAvailabilityAndRejectsSecondReturn() throws Exception {
        long bookId = books.findAll().get(1).getId();
        long memberId = members.findAll().get(0).getId();
        long borrowingId = borrow(memberId, bookId, "2026-11-10");

        mockMvc.perform(put("/api/borrowings/{id}/return", borrowingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"returnedOn\":\"2026-11-12\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnedOn").value("2026-11-12"))
                .andExpect(jsonPath("$.book.available").value(true));

        mockMvc.perform(put("/api/borrowings/{id}/return", borrowingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"returnedOn\":\"2026-11-13\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void listsOnlyActiveLoansForMember() throws Exception {
        long memberId = members.findAll().get(1).getId();
        long first = borrow(memberId, books.findAll().get(2).getId(), "2026-11-01");
        borrow(memberId, books.findAll().get(3).getId(), "2026-11-02");
        mockMvc.perform(put("/api/borrowings/{id}/return", first)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"returnedOn\":\"2026-11-05\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/members/{memberId}/borrowings", memberId)
                        .queryParam("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].returnedOn").doesNotExist());
    }

    @Test
    void chargesFiftyCentsForEachWholeOverdueDay() throws Exception {
        long borrowingId = borrow(
                members.findAll().get(2).getId(),
                books.findAll().get(4).getId(),
                "2026-10-01");

        mockMvc.perform(get("/api/borrowings/{id}/late-fee", borrowingId)
                        .queryParam("asOf", "2026-10-19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.lateDays").value(4))
                .andExpect(jsonPath("$.amount").value(2.00));
    }

    private long borrow(long memberId, long bookId, String borrowedOn) throws Exception {
        String response = mockMvc.perform(post("/api/members/{memberId}/borrowings", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowRequest(bookId, borrowedOn)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(response, "$.id");
        return id.longValue();
    }

    private String borrowRequest(long bookId, String borrowedOn) {
        return "{\"bookId\":" + bookId + ",\"borrowedOn\":\"" + borrowedOn + "\"}";
    }
}
