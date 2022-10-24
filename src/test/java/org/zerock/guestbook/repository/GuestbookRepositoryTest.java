package org.zerock.guestbook.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.zerock.guestbook.entity.Guestbook;
import org.zerock.guestbook.entity.QGuestbook;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GuestbookRepositoryTest {

    @Autowired
    private GuestbookRepository guestbookRepository;

    @Test
    public void insertDummies(){
        IntStream.rangeClosed(1,300).forEach(i ->{
            Guestbook guestbook = Guestbook.builder()
                    .title("Title...." + i)
                    .content("Content...." + i)
                    .writer("user" + (i % 10))
                    .build();
            System.out.println(guestbookRepository.save(guestbook));
        });
    }

    @Test
    public void updateTest(){
        Optional<Guestbook> result = guestbookRepository.findById(300L);

        if(result.isPresent()){
            Guestbook guestbook = result.get();

            guestbook.changeTitle("Change Title....");
            guestbook.changeContent("Change Content....");

            guestbookRepository.save(guestbook);
        }
    }

//    쿼리dsl테스트
    @Test
    public void testQuery1() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());
        //1. q도메인 클래스 얻어옴 -> 엔티티 클래스에 선언된 title content같은 필드들을 변수로 사용 가능
        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = "1";
        //2. BooleanBuilder는 where문에 들어가는 조건들을 넣어주는 컨테이너라고 간주
        BooleanBuilder builder = new BooleanBuilder();
        //3. 원하는 조건은 필드값과 같이 결합해서 생성. BooleanBuilder안에 들어가는 값은 자바에 있는 predict타입 아님 주의
        BooleanExpression expression = qGuestbook.title.contains(keyword);
        //4. 만들어진 조건은 where문에 and나 or같은 키워드와 결합시킴
        builder.and(expression); //4
        //5. BooleanBuilder는 레포지토리에 추가된 인터페이스의 findAll()을 사용가능
        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);
        // 페이지 처리와 동시에 검색 처리 가능해짐
        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }

    @Test
    public void testQuery2() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = "1";

        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression exTitle = qGuestbook.title.contains(keyword);
        BooleanExpression exContent = qGuestbook.title.contains(keyword);

        //1
        BooleanExpression exAll = exTitle.or(exContent);
        //2
        builder.and(exAll);
        //3
        builder.and(qGuestbook.gno.gt(0L));

        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });

    }
}