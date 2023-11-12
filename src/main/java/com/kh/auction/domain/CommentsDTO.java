package com.kh.auction.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentsDTO {
    private int commentNo;
    private String content;
    private Date commentDate;
    private int commentParent;
    private int auctionNo;
    private Member member;
    private int parent;
    private List<Comments> replies = new ArrayList<>();
}