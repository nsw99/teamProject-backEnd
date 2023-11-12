package com.kh.auction.repo;

import com.kh.auction.domain.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CommentsDAO extends JpaRepository<Comments, Integer> {

    // 게시글 1개에 따른 댓글 전체 조회 - SELECT * FROM comments WHERE auction_no = ?
    @Query(value = "SELECT * FROM comments WHERE auction_no = :no", nativeQuery = true)
    List<Comments> findByBoardComments(int no);


}
