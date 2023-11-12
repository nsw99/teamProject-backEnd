package com.kh.auction.controller;

import com.kh.auction.domain.Comments;
import com.kh.auction.domain.CommentsDTO;
import com.kh.auction.domain.Member;
import com.kh.auction.domain.Replies;
import com.kh.auction.service.CommentsService;
import com.kh.auction.service.MemberService;
import jakarta.persistence.Column;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins={"*"}, maxAge = 6000)
@Slf4j
public class CommentsController {

    @Autowired
    private CommentsService service;

    @Autowired
    private CommentsService comment;

    @Autowired
    private MemberService memberService;

    // 게시글 1개에 따른 댓글 전체 조회 : GET - http://localhost:8080/api/auctionBoard/{id}/comments


    // 댓글 추가 : POST - http://localhost:8080/api/auctionBoard/comments
    @PostMapping("/comments")
    public ResponseEntity<Comments> create(@RequestBody Comments comments) {
        return ResponseEntity.status(HttpStatus.OK).body(service.create(comments));
    }

    // 댓글 수정 : PUT - http://localhost:8080/api/auctionBoard/comments
    @PutMapping("/user/comments")
    public ResponseEntity<Comments> update(@AuthenticationPrincipal String id, @RequestBody Comments comments) {
        Member member =new Member();
        member.setId(id);

        comments.setMemberId(member);
        return ResponseEntity.status(HttpStatus.OK).body(service.update(comments));
    }


    // 댓글 삭제 : DELETE - http://localhost:8080/api/video/comment/1
    @DeleteMapping("/user/comment/{id}")
    public ResponseEntity<Comments> deleteComment(@PathVariable int id) {
        log.info(id+"★");
        return ResponseEntity.status(HttpStatus.OK).body(comment.delete(id));
    }


    @GetMapping("/comments")
    public ResponseEntity<List<Comments>> showAll() {
        return ResponseEntity.status(HttpStatus.OK).body(service.showAll());
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<Comments> show(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.show(id));
    }

    @GetMapping("/public/{id}/comment")
    public ResponseEntity<List<CommentsDTO>> videoCommentList(@PathVariable int id) {
        List<Comments> topList = comment.getAllTopLevelComments(id);
//        log.info("top : " + topList);

        List<CommentsDTO> response = new ArrayList<>();

        for(Comments item : topList) {
            CommentsDTO dto = new CommentsDTO();
            dto.setAuctionNo(item.getAuctionNo());
            dto.setCommentNo(item.getCommentNo());
            dto.setContent(item.getContent());
            dto.setMember(item.getMemberId());
            List<Comments> result = comment.getRepliesByCommentId(item.getCommentNo(), id);
            dto.setReplies(result);
            response.add(dto);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/public/{pno}/{id}/recomment")
    public ResponseEntity<List<CommentsDTO>> videoreCommentList(@PathVariable int pno, @PathVariable int id) {
        List<Comments> recomments = comment.getreCommentId(pno, id);
        List<CommentsDTO> response = new ArrayList<>();

        for (Comments item : recomments) {
            CommentsDTO dto = new CommentsDTO();
            dto.setAuctionNo(item.getAuctionNo());
            dto.setCommentNo(item.getCommentNo());
            dto.setContent(item.getContent());
            dto.setMember(item.getMemberId());
            dto.setParent(item.getCommentParent());
            response.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    //댓글 작성
    @PostMapping("/user/comment/{no}")
    public ResponseEntity<Comments> createComment(@RequestBody Comments vo, @AuthenticationPrincipal String id, @PathVariable int no) {
        Member member = new Member();
        member.setId(id);
        vo.setMemberId(member);
        vo.setAuctionNo(no);
        return ResponseEntity.status(HttpStatus.OK).body(comment.create(vo));
    }

    @PostMapping(value = {"/user/recomment/{no}/{pno}" , "/user/recomment/{no}"})
    public ResponseEntity<Comments> createComment(@RequestBody Comments vo, @AuthenticationPrincipal String id, @PathVariable int no, @PathVariable(required = false) Integer pno) {
        Member member = new Member();
        member.setId(id);
        vo.setMemberId(member);
        vo.setAuctionNo(no);
        vo.setCommentParent(pno);


        return ResponseEntity.status(HttpStatus.OK).body(service.create(vo));
    }

}
