package edu.yorgo.library.repository;

import edu.yorgo.library.domain.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByMemberIdAndBookIdAndCancelledOnIsNull(Long memberId, Long bookId);

    List<Reservation> findByMemberIdAndCancelledOnIsNullOrderByReservedOnAsc(Long memberId);

    Optional<Reservation> findByIdAndMemberIdAndCancelledOnIsNull(Long id, Long memberId);
}
