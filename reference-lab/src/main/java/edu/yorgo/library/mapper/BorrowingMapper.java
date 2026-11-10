package edu.yorgo.library.mapper;

import edu.yorgo.library.domain.Borrowing;
import edu.yorgo.library.dto.BorrowingResponse;
import org.springframework.stereotype.Component;

@Component
public class BorrowingMapper {

    private final BookMapper bookMapper;
    private final MemberMapper memberMapper;

    public BorrowingMapper(BookMapper bookMapper, MemberMapper memberMapper) {
        this.bookMapper = bookMapper;
        this.memberMapper = memberMapper;
    }

    public BorrowingResponse toResponse(Borrowing borrowing) {
        return new BorrowingResponse(
                borrowing.getId(),
                bookMapper.toResponse(borrowing.getBook()),
                memberMapper.toResponse(borrowing.getMember()),
                borrowing.getBorrowedOn(),
                borrowing.getDueDate(),
                borrowing.getReturnedOn());
    }
}
