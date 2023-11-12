package com.kh.auction.repo;

import com.kh.auction.domain.Replies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepliesDAO extends JpaRepository<Replies, Integer> {
}
