package com.kh.auction.repo;

import com.kh.auction.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryDAO extends JpaRepository<Category, Integer> {
    @Query(value="SELECT * FROM auction_board WHERE auction_no:auction_no",nativeQuery = true)
    List<Category> findByAuctionNo (int auction_no);

}