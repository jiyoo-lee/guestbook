package com.jeeyu.guestbook.controller;

import com.jeeyu.guestbook.domain.GuestBookDTO;
import com.jeeyu.guestbook.domain.PageRequestDTO;
import com.jeeyu.guestbook.service.GuestBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
@RequiredArgsConstructor
public class GuestBookController {

    //서비스 객체 주입
    private final GuestBookService guestBookService;

    @GetMapping({"/"})
    public String list() {
        log.info("/");
        return "redirect:/guestbook/list";
    }

    //void를 리턴하면 요청 URL이 View의 이름이 됩니다.
    @GetMapping({"guestbook/list"})
    public void list2(PageRequestDTO pageRequestDTO, Model model) {
        log.info("GuestBookController list ===> {} " + pageRequestDTO.toString());

        //서비스 메서드 호출
        //result의 dtoList에 DTO의 List가 있고 result의 pageList에 페이지 번호의 List가 존재
        model.addAttribute("result", guestBookService.getList(pageRequestDTO));
    }

    //등록 요청을 하면 GET 방식으로 처리하는 메서드 - 등록 페이지로 이동
    @GetMapping("/guestbook/register")
    public void register() {
        log.info("GuestBookController register ===> ");
    }

    //등록 요청을 POST 방식으로 처리하는 메서드 - 등록 수행
    @PostMapping("/guestbook/register")
    public String register(GuestBookDTO guestBookDTO, RedirectAttributes redirectAttributes) {
        log.info("GuestBookController register ===> {}", guestBookDTO);
        // 등록 요청 처리
        Long gno = guestBookService.register(guestBookDTO);

        //데이터 저장
        redirectAttributes.addFlashAttribute("msg", gno + "등록");

        // 목록 보기로 리다이렉트
        return "redirect:/guestbook/list";
    }
    
    //ModelAttribute 매개변수를 결과 페이지에 넘겨줄때 사용
    @GetMapping({"/guestbook/read", "/guestbook/modify"})
    public void read(long gno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO,
                     Model model) {
        log.info("gno: " + gno);
        GuestBookDTO dto = guestBookService.read(gno);
        model.addAttribute("dto", dto);
    }

    @PostMapping("/guestbook/modify")
    public String modify(GuestBookDTO dto,
                         @ModelAttribute("requestDTO") PageRequestDTO requestDTO,
                         RedirectAttributes redirectAttributes){
        log.info("post modify ====> {}", dto);
        log.info("dto: " + dto);
        guestBookService.modify(dto);
        redirectAttributes.addAttribute("page",requestDTO.getPage());
        redirectAttributes.addAttribute("gno",dto.getGno());
        return "redirect:/guestbook/read";
    }

    @PostMapping("/guestbook/remove")
    public String remove(long gno, RedirectAttributes redirectAttributes){
        log.info("gno: " + gno);
        guestBookService.remove(gno);
        redirectAttributes.addFlashAttribute("msg", gno + " 삭제");
        return "redirect:/guestbook/list";
    }


}
