package edu.yorgo.library.service;

import edu.yorgo.library.domain.Member;
import edu.yorgo.library.repository.MemberRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public List<Member> findAll() {
        return repository.findAll();
    }

    public Member findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
    }

    public Member create(Member member) {
        member.setId(null);
        return repository.save(member);
    }

    public Member update(Long id, Member replacement) {
        Member member = findById(id);
        member.setName(replacement.getName());
        member.setEmail(replacement.getEmail());
        member.setActive(replacement.isActive());
        return repository.save(member);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
