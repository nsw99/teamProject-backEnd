package com.kh.auction.service;


import com.kh.auction.domain.Member;
import com.kh.auction.repo.MemberDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;

@Service

public class MemberService {

    @Autowired
    private MemberDAO memberDAO;

    public List<Member> showAll() {
        return memberDAO.findAll();
    }

    public Member show(String id) {
        return memberDAO.findById(id).orElse(null);
    }

    public Member create(Member member) {
        return memberDAO.save(member);
    }

    public Member idDuplicate(String id) {
        Member target = memberDAO.findById(id).orElse(null);
        return target;
    }

    public Member nickDuplicate(String nick) {
        List<Member> target = memberDAO.findAll();
        for(Member newTarget : target){
            if(newTarget.getNick().equals(nick)){
                return newTarget;
            }
        }
        return null;
    }
    public Member passwordUpdate(String id, String password) {
        Member target = memberDAO.findById(id).orElse(null);
        if (target != null) {
            target.setPassword(password);
            return memberDAO.save(target);
        }
        return null;
    }

    public Member update(String id, int point) {
        Member target = memberDAO.findById(id).orElse(null);
        if (target != null) {
            target.setPoint(point);
            return memberDAO.save(target);
        }
        return null;
    }



    // 내정보 수정 api
    public Member userUpdate(String id, String nick, String phone, String email, String addr) {
        Member target = memberDAO.findById(id).orElse(null);
        if(target != null) {
            target.setNick(nick);
            target.setPhone(phone);
            target.setEmail(email);
            target.setAddr(addr);
            return memberDAO.save(target);
        }
        return null;
    }



    public Member delete(String id) {
        Member target = memberDAO.findById(id).orElse(null);
        memberDAO.delete(target);
        return target;
    }

    public Member getByCredentials(String id, String password, PasswordEncoder encoder) {
        Member member = memberDAO.findById(id).orElse(null);
        if (member != null && encoder.matches(password, member.getPassword())) {
            return member;
        }
        return null;
    }

}
