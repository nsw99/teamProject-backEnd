package com.kh.auction.service;

import com.kh.auction.domain.Interest;
import com.kh.auction.domain.QInterest;
import com.kh.auction.repo.InterestDAO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestService {

    @Autowired
    private InterestDAO interestDAO;
    @Autowired(required = true)
    private JPAQueryFactory queryFactory;

    private final QInterest qInterest = QInterest.interest;

    // 게시글 관심 등록
    public Interest create(Interest vo) {
        return interestDAO.save(vo);
    }

    // 게시글 관심 등록 취소
    @Transactional
    public long deleteInterest(String memberId, int auctionNo) {
        return queryFactory.delete(qInterest)
                .where(qInterest.member.id.eq(memberId))
                .where(qInterest.auction.auctionNo.eq(auctionNo))
                .execute();
    }




    public Interest delete(int auctionNo) {
        Interest target = interestDAO.findById(auctionNo).orElse(null);
        interestDAO.delete(target);
        return target;
    }

    // 자신이 관심등록한 게시물 목록 조회
    public List<Interest> findByMemberId(String id) {
        return interestDAO.findByMemberId(id);
    }

    // 게시물 관심등록 중복 체크
    public Integer duple(String memberId, int auctionNo) {
        return interestDAO.getInterestId(memberId, auctionNo);
    }

    public List<Interest> showAll() {
        return interestDAO.findAll();
    }

    public Interest show(int id) {
        return interestDAO.findById(id).orElse(null);
    }

    public Interest update(Interest interest) {
        return interestDAO.save(interest);
    }


}
