package edu.yorgo.library.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
class ReservationWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository books;

    @Autowired
    private MemberRepository members;

    @Test
    void createsListsAndCancelsOwnedReservation() throws Exception {
        long bookId = books.findAll().get(0).getId();
        long borrowerId = members.findAll().get(0).getId();
        long ownerId = members.findAll().get(1).getId();
        borrow(borrowerId, bookId);

        String body = reservationRequest(bookId);
        String response = mockMvc.perform(post("/api/members/{id}/reservations", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.member.id").value(ownerId))
                .andExpect(jsonPath("$.book.id").value(bookId))
                .andReturn().getResponse().getContentAsString();
        Number reservationId = JsonPath.read(response, "$.id");

        mockMvc.perform(post("/api/members/{id}/reservations", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/members/{id}/reservations", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(delete("/api/members/{memberId}/reservations/{reservationId}",
                        ownerId, reservationId.longValue()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/members/{id}/reservations", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(delete("/api/members/{memberId}/reservations/{reservationId}",
                        ownerId, reservationId.longValue()))
                .andExpect(status().isNotFound());
    }

    @Test
    void enforcesAvailabilityActiveMembershipOwnershipAndMissingResources() throws Exception {
        long availableBookId = books.findAll().get(1).getId();
        var inactiveMember = members.findAll().get(2);
        inactiveMember.setActive(false);
        members.saveAndFlush(inactiveMember);

        mockMvc.perform(post("/api/members/{id}/reservations", members.findAll().get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservationRequest(availableBookId)))
                .andExpect(status().isConflict());

        long unavailableBookId = books.findAll().get(2).getId();
        borrow(members.findAll().get(0).getId(), unavailableBookId);
        mockMvc.perform(post("/api/members/{id}/reservations", inactiveMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservationRequest(unavailableBookId)))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/members/{id}/reservations", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservationRequest(unavailableBookId)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/members/{id}/reservations", members.findAll().get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservationRequest(99999)))
                .andExpect(status().isNotFound());
    }

    @Test
    void hidesReservationFromNonOwnerDuringCancellation() throws Exception {
        long bookId = books.findAll().get(3).getId();
        long borrowerId = members.findAll().get(0).getId();
        long ownerId = members.findAll().get(1).getId();
        long otherMemberId = members.findAll().get(2).getId();
        borrow(borrowerId, bookId);

        String response = mockMvc.perform(post("/api/members/{id}/reservations", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservationRequest(bookId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Number reservationId = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/members/{memberId}/reservations/{reservationId}",
                        otherMemberId, reservationId.longValue()))
                .andExpect(status().isNotFound());
    }

    private void borrow(long memberId, long bookId) throws Exception {
        mockMvc.perform(post("/api/members/{memberId}/borrowings", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":" + bookId + ",\"borrowedOn\":\"2026-11-10\"}"))
                .andExpect(status().isCreated());
    }

    private String reservationRequest(long bookId) {
        return "{\"bookId\":" + bookId + ",\"reservedOn\":\"2026-11-10\"}";
    }
}
