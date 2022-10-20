package com.jeeyu.guestbook.service;

import com.jeeyu.guestbook.domain.GuestBookDTO;
import com.jeeyu.guestbook.domain.PageRequestDTO;
import com.jeeyu.guestbook.domain.PageResponseDTO;
import com.jeeyu.guestbook.entity.GuestBook;
import com.jeeyu.guestbook.entity.QGuestBook;
import com.jeeyu.guestbook.persistence.GuestBookRepository;
import com.querydsl.core.BooleanBuilder;
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
        String keyword = requestDTO.getKeyword();

        if(keyword != null) requestDTO.setKeyword(keyword.trim());

        //페이지 단위 요청을 위한 Pageable 객체 생성
        Pageable pageable = requestDTO
                .getPageable(Sort.by("gno")
                        .descending());
        BooleanBuilder booleanBuilder = getSearch(requestDTO);
        //데이터 베이스에서 조회
        Page<GuestBook> result = guestBookRepository.findAll(booleanBuilder, pageable);

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

    //검색 조건을 만들어주는 메서드
    private BooleanBuilder getSearch(PageRequestDTO requestDTO){

        //검색 항목 가져오기
        String type = requestDTO.getType();

        String keyword = requestDTO.getKeyword();

        BooleanBuilder builder = new BooleanBuilder();

        QGuestBook qGuestBook = QGuestBook.guestBook;

        //검색 조건이 없는 경우
        if(type == null || type.trim().length() == 0){
            return builder;
        }
        //검색 조건이 있는 경우
        BooleanBuilder conditionBuilder = new BooleanBuilder();
        
        //검색 조건에 따라 builder의 값이 달라짐
        if(type.contains("t")){
            conditionBuilder.or(qGuestBook.title.contains(keyword));
        }
        if(type.contains("c")){
            conditionBuilder.or(qGuestBook.content.contains(keyword));
        }
        if(type.contains("w")){
            conditionBuilder.or(qGuestBook.writer.contains(keyword));
        }
        builder.and(conditionBuilder);
        return builder;
    }

}
