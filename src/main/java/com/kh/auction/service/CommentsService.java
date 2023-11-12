package com.kh.auction.service;

import com.kh.auction.domain.Comments;
import com.kh.auction.domain.QComments;
import com.kh.auction.repo.CommentsDAO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
@Slf4j
public class CommentsService {

    @Autowired
    private CommentsDAO commentsDAO;

    @Autowired(required = true)
    private JPAQueryFactory queryFactory;

    private QComments QComments = com.kh.auction.domain.QComments.comments;

    // 게시글 1개에 따른 댓글 전체 조회
    public List<Comments> boardCommentsList(int no) {
        Comments target = commentsDAO.findById(no).orElse(null);
        if (target != null) {
            return commentsDAO.findByBoardComments(no);
        }
        return null;
    }


    public List<Comments> showAll() {
        return commentsDAO.findAll();
    }

    public Comments show(int id) {
        return commentsDAO.findById(id).orElse(null);
    }

    public Comments create(Comments comments) {
        return commentsDAO.save(comments);
    }

    public Comments update(Comments comments) {
        return commentsDAO.save(comments);
    }

    public Comments delete(int id) {
        Comments data = commentsDAO.findById(id).orElse(null);
        commentsDAO.delete(data);
        return data;
    }



    public List<Comments> findByComments(int id) {
        return commentsDAO.findByBoardComments(id);
    }

    public List<Comments> getAllTopLevelComments(int auctionNo) {
        return queryFactory.selectFrom(QComments)
                .where(QComments.commentParent.isNull())
                .where(QComments.auctionNo.eq(auctionNo))
                .orderBy(QComments.commentDate.desc())
                .fetch();
    }

    public List<Comments> getRepliesByCommentId(Integer parentId, int auctionNo) {
        return queryFactory.selectFrom(QComments)
                .where(QComments.commentParent.eq(parentId))
                .where(QComments.auctionNo.eq(auctionNo))
                .orderBy(QComments.commentDate.asc())
                .fetch();
    }

    public List<Comments> getreCommentId(int parentId, int auctionNo) {
        return queryFactory.selectFrom(QComments)
                .where(QComments.commentParent.eq(parentId)
                .and(QComments.auctionNo.eq(auctionNo)))
                .orderBy(QComments.commentDate.asc())
                .fetch();
    }


}