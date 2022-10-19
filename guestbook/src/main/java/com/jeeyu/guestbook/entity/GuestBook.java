package com.jeeyu.guestbook.entity;

import lombok.*;

import javax.persistence.*;
import java.nio.file.LinkOption;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GuestBook extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long gno;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1500, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;


    //수정을 위한 메서드 : getter만 존재하므로 수정이 필요한 부분에만 적용
    public void changeTitle(String title){
        this.title = title;
    }

    public void changeContent(String content){
        this.content = content;
    }

}
