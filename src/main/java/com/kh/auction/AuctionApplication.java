package com.kh.auction;

import com.kh.auction.controller.MemberController;
import com.kh.auction.domain.Member;
import com.kh.auction.domain.MemberDTO;
import com.kh.auction.user.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AuctionApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionApplication.class, args);
    }

    @Bean
        public CommandLineRunner commandLineRunner (MemberController memberController){
            return args -> {
                var admin = MemberDTO.builder()
                        .id("user12")
                        .password("user12")
                        .name("admin7")
                        .nick("admin7")
                        .email("3123@mail.com")
                        .phone("12345")
                        .sphone("456")
                        .addr("한글비석로2")
                        .birthday("45667")
                        .role(Role.USER)
                        .build();
                System.out.println("admin token " + memberController.userCreate(admin));
            };
}
    }
