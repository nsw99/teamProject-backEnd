package com.kh.auction.controller;

import com.kh.auction.domain.AuctionBoard;
import com.kh.auction.domain.Category;
import com.kh.auction.domain.Member;
//import com.kh.auction.domain.MemberDTO;
import com.kh.auction.domain.MemberDTO;
import com.kh.auction.security.JwtAuthenticationFilter;
import com.kh.auction.security.TokenProvider;
import com.kh.auction.security.WebSecurityConfig;
import com.kh.auction.service.AuctionBoardService;
import com.kh.auction.service.CategoryService;
import com.kh.auction.service.MemberService;
import com.kh.auction.user.Role;
import jakarta.servlet.http.Cookie;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins={"*"}, maxAge = 6000)

public class MemberController {
    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuctionBoardService auctionBoardService;

    @Autowired
    private CategoryService categoryService;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @GetMapping("/public")
    public ResponseEntity<List<Member>> showAll() {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.showAll());
    }

    @GetMapping("/user/show")
    public ResponseEntity<Member> show(@AuthenticationPrincipal String id) {
        try {
            log.info("Attempting to show member with id: {}", id);

            Member member = memberService.show(id);

            log.info("Successfully retrieved member: {}", member);

            return ResponseEntity.status(HttpStatus.OK).body(member);
        } catch (Exception e) {
            log.error("Error while processing show request", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    // 유저들 회원가입
    @PostMapping("/public/user/create")
    public ResponseEntity userCreate(@RequestBody MemberDTO dto) {
        Member member = Member.builder()
                .id(dto.getId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .nick(dto.getNick())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .sphone(dto.getSphone())
                .addr(dto.getAddr())
                .birthday(dto.getBirthday())
                .role(Role.USER)
                .build();
        Member registerMember = memberService.create(member);
        return ResponseEntity.status(HttpStatus.OK).body(registerMember);
    }

    // 어드민 회원가입
    @PostMapping("/public/admin/create")
    public ResponseEntity AdminCreate(@RequestBody MemberDTO dto) {
        Member vo = new Member();
        Member member = Member.builder()
                .id(dto.getId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .nick(dto.getNick())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .sphone(dto.getSphone())
                .addr(dto.getAddr())
                .authority("ROLE_ADMIN")
                .role(dto.getRole())
                .build();
        // 서비스를 이용해 리포지터리에 유저 저장
        Member registerMember = memberService.create(member);

        return ResponseEntity.status(HttpStatus.OK).body(registerMember);
    }


    // 아이디 중복
    @PostMapping("/public/idDuplicate")
    public ResponseEntity<Map<String, Boolean>> idDuplicate(@RequestParam(name = "id") String id) {
        boolean isDuplicate = memberService.idDuplicate(id) == null;
        Map<String, Boolean> response = new HashMap<>(); // 혹은 boolean 클래스 만들어서 객체지향성 높이기
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 닉네임 중복

    @PostMapping("/public/nickDuplicate")
    public ResponseEntity<Map<String, Boolean>> nickDuplicate(@RequestParam(name = "nick") String nick) {
        boolean isDuplicate = memberService.nickDuplicate(nick) == null;
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @DeleteMapping("/user/{id}")
    public ResponseEntity<Member> delete(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.delete(id));
    }

    @PostMapping("/public/signin")
    public ResponseEntity Login(@RequestBody MemberDTO dto){
        Member member = memberService.getByCredentials(dto.getId(), dto.getPassword(), passwordEncoder);
        if(member!=null){
            String token = tokenProvider.create(member);
            MemberDTO responseDTO = MemberDTO.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .addr(member.getAddr())
                    .nick(member.getNick())
                    .email(member.getEmail())
                    .phone(member.getPhone())
                    .sphone(member.getSphone())
                    .token(token)
                    .point(member.getPoint())
                    .authority(member.getAuthority())
                    .email(member.getEmail())
                    .singupDate(member.getSingupDate())
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }else {
            return ResponseEntity.badRequest().build();
        }

    }


    // 비밀번호 잊어먹었을때
    @PutMapping("/public/updatePassword")
    public ResponseEntity<Member> updatePassword(@RequestBody MemberDTO dto){
        String id = dto.getId();
        String password = dto.getPassword();
        String birthday = dto.getBirthday();

        Member existMember = memberService.show(id);
        if(existMember !=null){
            if(existMember.getBirthday().equals(birthday)){
                Member result = memberService.passwordUpdate(id,passwordEncoder.encode(password));
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);

    }

    // 포인트 api
    @PutMapping("/user/point")
    public ResponseEntity<Member> updatePoint(@AuthenticationPrincipal String id, @RequestBody Member member) {
        int point = member.getPoint();
        Member existMember = memberService.show(id);
        if (existMember != null) {
            existMember.setPoint(existMember.getPoint()+ point);
            log.info(existMember.getPoint()+"ss"+point);
            Member result = memberService.update(id,existMember.getPoint());
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    //최종입찰
    @PutMapping("/user/buyerPoint")
    public ResponseEntity<Member> updateBuyerPoint(@AuthenticationPrincipal String id, @RequestParam int point) {
        Member existMember = memberService.show(id);
        if (existMember != null) {
            existMember.setPoint(point);
            Member result = memberService.update(id,existMember.getPoint());
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/public/updateCategory")
    public ResponseEntity updateCategory(@RequestParam int no){
        AuctionBoard auctionBoard = auctionBoardService.show(no);
        if (auctionBoard != null) {
            auctionBoard = auctionBoardService.updateCategoryNo(no,'Y');
            log.info(auctionBoard+"");
            return ResponseEntity.status(HttpStatus.OK).body(auctionBoard);
            }
        return null;
    }



    // 내 정보 수정 api
    @PutMapping("/user/updateuser")
    public ResponseEntity<Member> updateUser(@AuthenticationPrincipal String id , @RequestBody Member member) {

        String nick = member.getNick();
        String phone = member.getPhone();
        String email = member.getEmail();
        String addr = member.getAddr();
        Member existMember = memberService.show(id);

        existMember.setNick(nick);
        existMember.setPhone(phone);
        existMember.setEmail(email);
        existMember.setAddr(addr);

        Member result = memberService.userUpdate(id, existMember.getNick(), existMember.getPhone(), existMember.getEmail(), existMember.getAddr());
        if(result != null) {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    // 비밀번호 확인
    @PostMapping("/user/pwdCheck")
    public ResponseEntity<Boolean> checkPassword(@AuthenticationPrincipal String id ,@RequestBody MemberDTO dto) {
        Member existMember = memberService.show(id);
        String password = dto.getPassword();
        boolean result = false;

        if(existMember.getPassword() !=null){
            if(passwordEncoder.matches(password, existMember.getPassword())){
                result = true;
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    // 내정보에서 비밀번호 변경
    @PutMapping("/user/pwdUpdate")
    public ResponseEntity<Member> updatePassword(@AuthenticationPrincipal String id ,@RequestBody MemberDTO dto){
        Member existMember = memberService.show(id);
        log.info(existMember.getPassword()+"기존 비밀번호");
        String password = dto.getPassword();
        log.info(password);
        
        Member result = memberService.passwordUpdate(existMember.getId(),passwordEncoder.encode(password));

        log.info(result+"");
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }


    }



    // 포인트 충전 내역
//    @PutMapping("/user/pointAdd")
//    public ResponseEntity

    // 내 결제 기록 최신순
//    @GetMapping("public/mypayList/new")
//    public ResponseEntity<List<>>

    // 결제 기록 페이지
//    @GetMapping("/user/pointList")
//    public ResponseEntity<>

