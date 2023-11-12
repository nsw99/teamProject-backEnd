package com.kh.auction.controller;

import com.kh.auction.domain.*;
import com.kh.auction.service.AuctionBoardService;
import com.kh.auction.service.InterestService;
import com.kh.auction.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins={"*"}, maxAge = 6000)
public class InterestController {

    @Autowired
    private InterestService interestService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuctionBoardService auctionBoardService;


    // 게시글 관심 등록 : POST - http://localhost:8080/user/addList
    @PostMapping("/user/addList")
    public ResponseEntity<Interest> addList(@AuthenticationPrincipal String id, @RequestParam int auctionNo) {
        Interest vo = new Interest();
        log.info(id + "wdwdw" + auctionNo);
        Member existMember = memberService.show(id);
        AuctionBoard auctionBoard = auctionBoardService.show(auctionNo);
        auctionBoard.setAuctionNo(auctionNo);
        vo.setMember(existMember);
        vo.setAuction(auctionBoard);

        return ResponseEntity.status(HttpStatus.OK).body(interestService.create(vo));
    }

    // 중복확인
    @PostMapping("/user/interestDuplicate")
    public ResponseEntity<Boolean> duplicate(@AuthenticationPrincipal String memberId, @RequestParam int auctionNo){
        Boolean num = interestService.duple(memberId, auctionNo) != null;
        log.info(auctionNo+memberId);

        return ResponseEntity.status(HttpStatus.OK).body(num);
    }

    // 게시글 관심 등록 취소 : DELETE - http://localhost:8080/api/user/checkDelete
    @DeleteMapping("/user/checkDelete")
    public ResponseEntity InterestDelete(@AuthenticationPrincipal String memberId, @RequestParam int auctionNo) {
        log.info(memberId +"관심 취소" + auctionNo);
        return ResponseEntity.status(HttpStatus.OK).body(interestService.deleteInterest(memberId, auctionNo));
    }
    //wdw

    // 게시글 관심 등록 한번에 취소 : DELETE - http://localhost:8080/api/user/checkDeleteList
    @DeleteMapping("/user/checkDeleteList")
    @Transactional
    public ResponseEntity<Interest> checkAllDelete(@AuthenticationPrincipal String memberId, @RequestParam List<Integer> list) {
        log.info(memberId +"관심 취소" + list);
        for(int no : list) {
            interestService.deleteInterest(memberId, no);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }



    // 자신이 관심 등록한 게시글 목록 조회 : GET - http://localhost:8080/api/user/myInterestList
    @GetMapping("/user/myInterestList")
    public ResponseEntity<List<InterestDTO>> showInterestList(@AuthenticationPrincipal String id) {
        List<Interest> interests = interestService.findByMemberId(id);

        List<InterestDTO> interestDTOs = new ArrayList<>();

        for (Interest interest : interests) {
            InterestDTO interestDTO = InterestDTO.builder()
                    .interestNo(interest.getInterestNo())
                    .member(interest.getMember().getId())
                    .auction(interest.getAuction())
                    .build();
            interestDTOs.add(interestDTO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(interestDTOs);
    }



    @GetMapping("/interest")
    public ResponseEntity<List<Interest>> showAll() {
        return ResponseEntity.status(HttpStatus.OK).body(interestService.showAll());
    }
//
//    @GetMapping("/interest/{id}")
//    public ResponseEntity<Interest> show(@PathVariable int id) {
//        return ResponseEntity.status(HttpStatus.OK).body(service.show(id));
//    }
//
//    @PutMapping("/interest")
//    public ResponseEntity<Interest> update(@PathVariable Interest interest) {
//        Interest result = service.update(interest);
//        if(result != null){
//            return ResponseEntity.status(HttpStatus.OK).body(result);
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//    }

}