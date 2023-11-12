package com.kh.auction.service;

import com.kh.auction.domain.AuctionBoard;
import com.kh.auction.repo.AuctionBoardDAO;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AuctionBoardService {

    @Autowired
    private AuctionBoardDAO auctionBoardDAO;


    public Page<AuctionBoard> showAll(Pageable pageable, BooleanBuilder builder) {
//        return auctionBoardDAO.findAll(builder, pageable);
        return auctionBoardDAO.findAll(builder, pageable);
    }

    public List<AuctionBoard> showAll(){
        return auctionBoardDAO.findAll();
    }

    public Page<AuctionBoard> Search(String keyword, Pageable pageable){
        return auctionBoardDAO.findByAuctionTitleContaining(keyword, pageable);
    }

    public AuctionBoard show(int no) {
        return auctionBoardDAO.findById(no).orElse(null);
    }

    public Page<AuctionBoard> showAll(Pageable pageable) {
        return auctionBoardDAO.findAll(pageable);
    }

    public AuctionBoard create(AuctionBoard auctionBoard) {
        return auctionBoardDAO.save(auctionBoard);
    }

    public AuctionBoard update(AuctionBoard auctionBoard) {
        AuctionBoard target = auctionBoardDAO.findById(auctionBoard.getAuctionNo()).orElse(null);
        if (target != null) {
            return auctionBoardDAO.save(auctionBoard);
        }
        return null;
    }

    public AuctionBoard delete(int id) {
        AuctionBoard category = auctionBoardDAO.findById(id).orElse(null);
        auctionBoardDAO.delete(category);
        return category;
    }

    public List<AuctionBoard> findByCategoryNo(int code) {
        return auctionBoardDAO.findByCategoryNo(code);
    }

    public List<AuctionBoard> getAuctionBoardsOrderByAttendNoDesc() {
        return auctionBoardDAO.findAllOrderByAuctionAttendNoDesc();
    }

    public AuctionBoard updatePrice(int no, int price, String id) {
        AuctionBoard target = auctionBoardDAO.findById(no).orElse(null);
        if (target != null) {
            target.setCurrentPrice(price);
            target.setBuyerPoint(price);
            target.setBuyerId(id);
            log.info(id);
            return auctionBoardDAO.save(target);
        }
        return null;
    }

    // Hot 게시글
    public List<AuctionBoard> findByHot(int no) {
        List<AuctionBoard> allAuctionBoards = auctionBoardDAO.findAll();
        Date currentDate = new Date(); // 현재 날짜 가져오기

        // 정렬
        allAuctionBoards.sort(Comparator.comparing(AuctionBoard::getCurrentNum).reversed());

        List<AuctionBoard> filteredAuctionBoards = new ArrayList<>();

        for (AuctionBoard board : allAuctionBoards) {
            Date auctionEndDate = board.getAuctionEndDate();
            // 게시글의 종료 날짜가 현재 날짜 이전이 아닌 경우에만 목록에 추가
            if (auctionEndDate == null || !auctionEndDate.before(currentDate)) {
                filteredAuctionBoards.add(board);
            }

            // 목록이 지정된 개수에 도달하면 루프 종료
            if (filteredAuctionBoards.size() >= no) {
                break;
            }
        }

        return filteredAuctionBoards;
    }

    // New 게시글
    public List<AuctionBoard> findByNew(int no) {
        List<AuctionBoard> allAuctionBoards = auctionBoardDAO.findAll();
        Date currentDate = new Date(); // 현재 날짜 가져오기

        // 정렬
        allAuctionBoards.sort(Comparator.comparing(AuctionBoard::getAuctionDate).reversed());

        List<AuctionBoard> filteredAuctionBoards = new ArrayList<>();

        for (AuctionBoard board : allAuctionBoards) {
            Date auctionEndDate = board.getAuctionEndDate();
            // 게시글의 종료 날짜가 현재 날짜 이전이 아닌 경우에만 목록에 추가
            if (auctionEndDate == null || !auctionEndDate.before(currentDate)) {
                filteredAuctionBoards.add(board);
            }

            // 목록이 지정된 개수에 도달하면 루프 종료
            if (filteredAuctionBoards.size() >= no) {
                break;
            }
        }

        return filteredAuctionBoards;
    }

    public List<AuctionBoard> findByChannelCode(int code) {
        return auctionBoardDAO.findByCategoryNo(code);
    }

    // 조회수 +1
    public AuctionBoard updateCheckNo(int no) {
        AuctionBoard auctionBoard = auctionBoardDAO.findById(no).orElse(null);
        if (auctionBoard != null) {
            auctionBoard.setAuctionCheckNo(auctionBoard.getAuctionCheckNo() + 1);
            return auctionBoardDAO.save(auctionBoard);
        }
        return null;
    }

    // 입찰횟수 +1
    public AuctionBoard updateCurrentNum(int no) {
        AuctionBoard auctionBoard = auctionBoardDAO.findById(no).orElse(null);
        if (auctionBoard != null) {
            auctionBoard.setCurrentNum(auctionBoard.getCurrentNum() + 1);
            auctionBoard.setAuctionCheckNo(auctionBoard.getAuctionCheckNo() - 1);
            return auctionBoardDAO.save(auctionBoard);
        }
        return null;
    }

    public AuctionBoard updateCategoryNo(int no, char end){
        AuctionBoard auctionBoard = auctionBoardDAO.findById(no).orElse(null);
        if (auctionBoard != null){
            auctionBoard.setAuctionEnd(end);
            return auctionBoardDAO.save(auctionBoard);
        }
        return null;
    }

    // 사용자 총 게시물 조회
    public Integer countAuctionByMemberId(String memberId) {
        return auctionBoardDAO.countAuctionByMemberId(memberId);
    }

}