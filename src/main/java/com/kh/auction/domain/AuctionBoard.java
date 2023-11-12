package com.kh.auction.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "AUCTION_BOARD")
public class AuctionBoard {

    @Id
    @Column(name = "auction_no")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auctionSeq") // GenerationType을 SEQUENCE로 변경
    @SequenceGenerator(name = "auctionSeq", sequenceName = "SEQ_AUCTION", allocationSize = 1) // sequenceName을 "AUCTION_NO_SEQ"로 변경
    private int auctionNo; // 경매 게시글 번호

    @Column(name = "auction_title")
    private String auctionTitle; // 경매 게시글 제목
    @Column(name = "auction_img")
    private String auctionImg; // 경매 게시글에 등록된 이미지 목록

    @Column(name = "auction_date")
    private Date auctionDate; // 경매 등록일
    @Column(name = "item_name")
    private String itemName; // 상품 이름
    @Column(name = "item_desc")
    private String itemDesc; // 상품 설명
    @Column(name = "current_price")
    private int currentPrice; // 현재 가격
    @Column(name = "auction_smoney")
    private int auctionSMoney; // 경매 시작가
    @Column(name = "auction_emoney")
    private int auctionEMoney; // 경매 최소 입찰가
    @Column(name = "auction_gmoney")
    private int auctionGMoney; // 경매 즉시 구매가
    @Column(name = "auction_nowbuy_y_n")
    private char auctionNowbuy; // 경매 즉시 구매 여부
    @Column(name = "auction_end_date")
    private Date auctionEndDate; // 경매 마감일 (경매 등록일 + 30일)

    @Column(name = "auction_check_no")
    private int auctionCheckNo; // 조회수
    @Column(name = "auction_attend_no")
    private int auctionAttendNo; // 입찰 참여 인원수

    @Column(name = "current_num")
    private int currentNum; // 입찰 횟수

    @Column(name = "buyer_id")
    private String buyerId;

    @Column(name = "buyer_point")
    private int buyerPoint;

    @Column(name = "auction_end")
    private char auctionEnd;

    @ManyToOne
    @JoinColumn(name="category_no")
    private Category category;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member memberId;
}