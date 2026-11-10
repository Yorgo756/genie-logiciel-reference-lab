package edu.yorgo.library.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private LocalDate reservedOn;
    private LocalDate cancelledOn;

    public Reservation() {
    }

    public Reservation(Book book, Member member, LocalDate reservedOn) {
        this.book = book;
        this.member = member;
        this.reservedOn = reservedOn;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getReservedOn() {
        return reservedOn;
    }

    public LocalDate getCancelledOn() {
        return cancelledOn;
    }

    public void setCancelledOn(LocalDate cancelledOn) {
        this.cancelledOn = cancelledOn;
    }
}
