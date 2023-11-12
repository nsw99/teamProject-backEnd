package com.kh.auction.service;

import com.kh.auction.domain.AuctionBoard;
import com.kh.auction.domain.Category;
import com.kh.auction.repo.AuctionBoardDAO;
import com.kh.auction.repo.CategoryDAO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryService {


    @Autowired
    private CategoryDAO categoryDAO;

//    @Autowired(required = true)
//    private JPAQueryFactory queryFactory;

//    private final QVideoComment qVideoComment = QVideoComment.videoComment;

    public List<Category> findByAuctionNo(int auctionNo){
        return categoryDAO.findByAuctionNo(auctionNo);
    }

    public List<Category> showAll() {
        return categoryDAO.findAll();
    }

    public Category show(int no) {
        return categoryDAO.findById(no).orElse(null);
    }

    public Category create(Category category) {
        return categoryDAO.save(category);
    }


    public Category update(Category category) {
        Category target = categoryDAO.findById(category.getCategoryNo()).orElse(null);
        if (target != null) {
            return categoryDAO.save(category);
        }
        return null;
    }

    public Category delete(int no) {
        Category category = categoryDAO.findById(no).orElse(null);
        categoryDAO.delete(category);
        return category;
    }




}