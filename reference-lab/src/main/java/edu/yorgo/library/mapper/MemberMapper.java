package edu.yorgo.library.mapper;

import edu.yorgo.library.domain.Member;
import edu.yorgo.library.dto.MemberRequest;
import edu.yorgo.library.dto.MemberResponse;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public MemberResponse toResponse(Member member) {
        return new MemberResponse(member.getId(), member.getName(), member.getEmail(), member.isActive());
    }

    public Member toEntity(MemberRequest request) {
        Member member = new Member(request.name(), request.email());
        member.setActive(request.active() == null || request.active());
        return member;
    }
}
