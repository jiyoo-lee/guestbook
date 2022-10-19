package com.jeeyu.guestbook.service;

import com.jeeyu.guestbook.domain.GuestBookDTO;
import com.jeeyu.guestbook.domain.PageRequestDTO;
import com.jeeyu.guestbook.domain.PageResponseDTO;
import com.jeeyu.guestbook.entity.GuestBook;
import com.jeeyu.guestbook.persistence.GuestBookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Log4j2
@Service
public class GuestBookServiceImpl implements GuestBookService {

    //final 쓰고 @RequiredArgsConstructor 써주면 자동 주입됨.
    private final GuestBookRepository guestBookRepository;

    @Override
    public Long register(GuestBookDTO dto) {
        log.info("데이터 삽입");
        log.info(dto);

        //repository에서 사용하기 위해서 DTO를 Entity로 변환
        GuestBook entity = dtoToEntity(dto);
        // 데이터 삽입
        GuestBook result = guestBookRepository.save(entity);
        // 삽입한 후 리턴받은 데이터의 gno 리턴
        return result.getGno();
    }

    @Override
    public PageResponseDTO<GuestBookDTO, GuestBook> getList(PageRequestDTO requestDTO) {
        //페이지 단위 요청을 위한 Pageable 객체 생성
        Pageable pageable = requestDTO
                .getPageable(Sort.by("gno")
                        .descending());

        //데이터 베이스에서 조회
        Page<GuestBook> result = guestBookRepository.findAll(pageable);

        //Entity를 DTO로 변환하기 위한 객체 생성
        Function<GuestBook, GuestBookDTO> fn = (entity -> entityToDTO(entity));

        //데이터 목록 생성
        return new PageResponseDTO<>(result,fn) ;
    }

    @Override
    public GuestBookDTO read(Long gno) {
        Optional<GuestBook> guestBook = guestBookRepository.findById(gno);
        return guestBook.isPresent()? entityToDTO(guestBook.get()): null;
    }

    @Override
    public void modify(GuestBookDTO dto) {
        //업데이트 하는 항목은 '제목', '내용'
        Optional<GuestBook> result = guestBookRepository.findById(dto.getGno());
        if(result.isPresent()) {
            GuestBook entity = result.get();
            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());
            guestBookRepository.save(entity);
        }
    }

    @Override
    public void remove(Long gno) {
        guestBookRepository.deleteById(gno);
    }
}
