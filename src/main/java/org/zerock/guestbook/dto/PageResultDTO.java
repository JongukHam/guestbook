package org.zerock.guestbook.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResultDTO<DTO,EN> { //dto entity를 받겠다
    private List<DTO> dtoList;

    private int totalPage;
    private int page;
    private int size;
    private int start,end;

    private boolean prev,next;

    private List<Integer> pageList;

    public PageResultDTO(Page<EN> result, Function<EN, DTO> fn) { //function 은 엔티티를 디티오로 변환해 주는 기능 en은 입력타입 dto는 리턴타입
        dtoList = result.stream().map(fn).collect(Collectors.toList()); //엔티티들을 디티오로 바꿔서 화면으로 전달해줌
        totalPage = result.getTotalPages();
        makePageList(result.getPageable());
    }

    private void makePageList(Pageable pageable) {
        this.page = pageable.getPageNumber() +1;
        this.size = pageable.getPageSize();

        //temp end page
        int tempEnd = (int)(Math.ceil(page/10.0))*10;

        start = tempEnd-9;
        prev = start > 1;
        end = totalPage > tempEnd ? tempEnd : totalPage;
        next = totalPage > tempEnd;
        pageList = IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList());
    }



}
