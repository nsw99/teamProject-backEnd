package com.kh.auction.repo;

import com.kh.auction.domain.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterestDAO extends JpaRepository<Interest, Integer> {

    // 자신이 관심 등록한 게시물 목록 조회 - SELECT * FROM interest WHERE id=?
    @Query(value = "SELECT * FROM interest WHERE member_id = :memberId", nativeQuery = true)
    List<Interest> findByMemberId(@Param("memberId") String id);

    // 관심 등록한 게시물 해제
    //DELETE FROM users WHERE id = 1
    @Query(value = "DELETE FROM interest WHERE member_id = :memberId AND auction_no = :auctionNo", nativeQuery = true)
    Interest deleteByDelete(@Param("memberId") String memberId, @Param("auctionNo") int auctionNo);

    // 관심 등록한 게시물 해제
    //DELETE FROM users WHERE id = 1
    @Query(value = "DELETE FROM interest WHERE member_id = :memberId AND auction_no = :auctionNo", nativeQuery = true)
    Interest deleteByDeleteList(@Param("memberId") String memberId, @Param("auctionNo") int auctionNo);

    // 중복 확인용 반환데이터
    @Query(value = "SELECT INTEREST_NO FROM INTEREST WHERE MEMBER_ID = :memberId AND auction_no = :auctionNo", nativeQuery = true)
    Integer getInterestId(@Param("memberId") String memberId, @Param("auctionNo") int auctionNo);

}
