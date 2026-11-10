package edu.yorgo.library.repository;

import edu.yorgo.library.domain.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
}
