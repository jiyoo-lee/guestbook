package com.jeeyu.guestbook;

import com.jeeyu.guestbook.domain.GuestBookDTO;
import com.jeeyu.guestbook.domain.PageRequestDTO;
import com.jeeyu.guestbook.domain.PageResponseDTO;
import com.jeeyu.guestbook.entity.GuestBook;
import com.jeeyu.guestbook.service.GuestBookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ServiceTest {

    @Autowired
    private GuestBookService guestBookService;


    //@Test
    public void insertTest(){
        GuestBookDTO dto = GuestBookDTO.builder()
                .title("삽입 테스트")
                .content("서비스에서 테스트")
                .writer("tester")
                .build();
        Long gno = guestBookService.register(dto);
        System.out.println("삽입된 번호 ----->" + gno);
    }

    @Test
    public void listTest(){
        //페이지 번호와
        PageRequestDTO pageRequestDTO
                = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();
        PageResponseDTO<GuestBookDTO, GuestBook> resultDTO
                = guestBookService.getList(pageRequestDTO);

        //확인
        for(GuestBookDTO dto : resultDTO.getDtoList()){
            System.out.println(dto);
        }

        //이전 페이지 번호와 다음 페이지 존재 여부
        System.out.println("이전 여부 ==> " + resultDTO.isPrev());
        System.out.println("다음 여부 ==>" + resultDTO.isNext());
        //전체 페이지 개수
        System.out.println("전체 페이지 개수:" + resultDTO.getTotalPage());
        resultDTO.getPageList().forEach(i ->{
            System.out.println(i);
        });
    }
}
