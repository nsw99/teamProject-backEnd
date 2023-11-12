package com.kh.auction.repo;

import com.kh.auction.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDAO extends JpaRepository<Member, String> {


}


