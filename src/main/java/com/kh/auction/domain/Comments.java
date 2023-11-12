package com.kh.auction.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Comments {

    @Id
    @Column(name = "comment_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "commentSequence")
    @SequenceGenerator(name = "commentSequence", sequenceName = "SEQ_COMMENT", allocationSize = 1)
    private int commentNo;

    @Column(name="content")
    private String content;

    @Column(name = "comment_date")
    private Date commentDate;

    @Column(name="comment_parent")
    private Integer commentParent;


//    @Column(name="comment_parent")
//    private int parent;

    //    @ManyToOne
//    @JoinColumn(name="auction_no")
    @Column(name="auction_No")
    private int auctionNo;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member memberId;

}