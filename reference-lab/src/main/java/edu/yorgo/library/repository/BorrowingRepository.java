package edu.yorgo.library.repository;

import edu.yorgo.library.domain.Borrowing;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    boolean existsByBookIdAndReturnedOnIsNull(Long bookId);

    List<Borrowing> findByMemberIdAndReturnedOnIsNullOrderByBorrowedOnAsc(Long memberId);
}
