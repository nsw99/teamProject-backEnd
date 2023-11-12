package com.kh.auction.domain;

import com.kh.auction.user.Role;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private String token;

    private String id;

    private String password;

    private String nick;

    private String name;

    private String birthday;

    private String phone; // 회원 전화번호

    private String sphone; // 회원 안심번호

    private String email;

    private String addr;

    private Date singupDate;

    private String authority;

    private int point;

    private Role role;

}
