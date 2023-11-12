package com.kh.auction.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepliesDTO {
    private int replyNo;
    private String replyContent;
    private Date replyDate;
    private int auctionNo;
    private Member memberId;
    private Comments comments;
}
