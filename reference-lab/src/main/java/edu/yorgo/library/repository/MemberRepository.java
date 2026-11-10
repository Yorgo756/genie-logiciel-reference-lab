package edu.yorgo.library.repository;

import edu.yorgo.library.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
