package com.kh.auction.controller;

import com.kh.auction.domain.*;
import com.kh.auction.service.CommentsService;
import com.kh.auction.service.RepliesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins={"*"}, maxAge = 6000)
@Slf4j
public class RepliesController {

    @Autowired
    private RepliesService service;
    @Autowired
    private CommentsService comment;

    @GetMapping("/Replies")
    public ResponseEntity<List<Replies>> showAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.showAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/Replies/{id}")
    public ResponseEntity<Replies> show(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.show(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/Replies")
    public ResponseEntity<Replies> create(@RequestBody Replies replies) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.create(replies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/Replies")
    public ResponseEntity<Replies> update(@RequestBody Replies replies) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.update(replies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @DeleteMapping("Replies/{id}")
    public ResponseEntity<Replies> delete(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.delete(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
//    @GetMapping("/public/{id}/recomment")
//    public ResponseEntity<List<RepliesDTO>> videoCommentList(@PathVariable int id) {
//        List<Comments> topList = comment.getAllTopLevelComments(id);
//        log.info("top : " + topList);
//
//        List<RepliesDTO> response = new ArrayList<>();
//
//        for(Comments item : topList) {
//            CommentsDTO dto = new CommentsDTO();
//            dto.setAuctionNo(item.getAuctionNo());
//            dto.setCommentNo(item.getCommentNo());
//            dto.setContent(item.getContent());
//            dto.setMember(item.getMemberId());
//
//            List<Comments> result = comment.getRepliesByCommentId(item.getCommentNo(), id);
//            dto.setReplies(result);
//            response.add(dto);
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

//    @PostMapping("/user/recomment/{no}/{pno}")
//    public ResponseEntity<Replies> createComment(@RequestBody Replies vo, @AuthenticationPrincipal String id, @PathVariable int no, @PathVariable int pno) {
//        Member member = new Member();
//        member.setId(id);
//        vo.setMemberId(member);
//        vo.setAuctionNo(no);
//        vo.setCommentNo(pno);
//        return ResponseEntity.status(HttpStatus.OK).body(service.create(vo));
//    }

}