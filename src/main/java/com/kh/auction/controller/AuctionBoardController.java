package com.kh.auction.controller;

import com.kh.auction.domain.*;
import com.kh.auction.domain.AuctionBoard;
import com.kh.auction.domain.Category;
import com.kh.auction.repo.AuctionBoardDAO;
import com.kh.auction.service.AuctionBoardService;
import com.kh.auction.service.CategoryService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins={"*"}, maxAge = 6000)
public class AuctionBoardController {

    @Autowired
    private AuctionBoardService auctionBoardService;

    @Autowired
    private CategoryService category;

    @Value("${team.upload.path}") // application.properties에 있는 변수
    private String uploadPath;

    @GetMapping(value = {"/public/auction/{categoryNo}" , "/public/auction"})
    public ResponseEntity<Map<String, Object>> BoardList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "category", required = false) Integer category,
            @RequestParam(name = "sortOption", defaultValue = "1") int sortOption,
            @PathVariable(name = "categoryNo", required = false) Integer categoryNo
    ) {
        // 정렬 방식에 따라 Sort 객체 생성
        Sort sort = getSortForOption(sortOption);

        Pageable pageable = PageRequest.of(page - 1, 8, sort);

        QAuctionBoard AuctionBoard = QAuctionBoard.auctionBoard;
        BooleanBuilder builder = new BooleanBuilder();

        if (category != null) {
            BooleanExpression expression = QAuctionBoard.auctionBoard.category.categoryNo.eq(category);
            builder.and(expression);
        }

        // auction_end가 "N"인 경우만 필터링
        builder.and(QAuctionBoard.auctionBoard.auctionEnd.eq('N'));

        Page<AuctionBoard> result = auctionBoardService.showAll(pageable, builder);

        List<AuctionBoard> auctionBoards = result.getContent();
        Map<String, Object> response = new HashMap<>();
        List<AuctionBoard> categoryResults = new ArrayList<>();

        for(AuctionBoard auctionBoard : auctionBoards){
            int no = auctionBoard.getCategory().getCategoryNo();
            if(categoryNo != null && no == categoryNo){
                categoryResults.add(auctionBoard);
            }
        }
        if(!categoryResults.isEmpty()){
            response.put("totalPages", result.getTotalPages());
            response.put("content", categoryResults);
        }else{
            response.put("totalPages", result.getTotalPages());
            response.put("content", result.getContent());
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private Sort getSortForOption(int sortOption) {
        switch (sortOption) {
            case 1:
                return Sort.by("auctionAttendNo").descending(); // 입찰 횟수 내림차순
            case 2:
                return Sort.by("auctionCheckNo").descending(); // 조회수 내림차순
            case 3:
                return Sort.by("auctionNo").ascending(); // 등록순
            case 4:
                return Sort.by("currentPrice").ascending(); // 낮은 가격순
            case 5:
                return Sort.by("currentPrice").descending(); // 높은 가격순
            default:
                return Sort.by("auctionNo").descending(); // 기본값: 경매 번호 내림차순
        }
    }

    // 게시글 조회 / 조회수 +1
    @GetMapping("/user/auction/{no}")
    public ResponseEntity<AuctionBoard> show(@PathVariable int no, HttpServletResponse response) {
        AuctionBoard auctionBoard = auctionBoardService.show(no);
        if (auctionBoard != null) {
            // 쿠키 생성 및 추가
//            String[] imagePaths = auctionBoard.getAuctionImg().split(",", 0);
//            Cookie cookie = new Cookie("recentlyView_" + no, imagePaths[0]);
//
//            log.info(auctionBoard.getAuctionImg());
//            cookie.setMaxAge(24 * 60 * 60 );
//            response.addCookie(cookie);
            auctionBoardService.updateCheckNo(no);

            return ResponseEntity.status(HttpStatus.OK).body(auctionBoard);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 쿠키처리 (백으로하려다 프론트로 넘어감.)
    @GetMapping("/user/recentView")
    public ResponseEntity<List<AuctionBoard>> getRecentlyView(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        List<AuctionBoard> auctionBoards = new ArrayList<>();

        if (cookies != null) {
            for (Cookie cookie : cookies) {

                if (cookie.getName().startsWith("recentlyView_")) {

                    int auctionNo = Integer.parseInt(cookie.getName().substring(("recentlyView_").length()));
                    AuctionBoard auctionBoard = auctionBoardService.show(auctionNo);

                    if (auctionBoard != null) {
                        auctionBoards.add(auctionBoard);
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(auctionBoards);
    }

    // 경매 입찰하기 / 입찰횟수 +1
    @PutMapping("/user/auction/{auctionNo}")
    public ResponseEntity<AuctionBoard> placeBid(@PathVariable int auctionNo, @RequestBody AuctionBoard auction , @AuthenticationPrincipal String id) {
        int price = auction.getCurrentPrice();
        // 경매 번호에 해당하는 경매 게시물을 조회
        AuctionBoard auctionBoard = auctionBoardService.show(auctionNo);

        if (auctionBoard != null) {
            // 현재 가격과 비교하여 유효한 입찰인 경우에만 업데이트
            if (price > auctionBoard.getCurrentPrice()) {
                auctionBoard.setCurrentPrice(price); // 새로운 입찰 가격으로 설정
                auctionBoardService.updatePrice(auctionNo, price, id);
                log.info(id);
                auctionBoardService.updateCurrentNum(auctionNo); // 입찰 횟수 업데이트
                return ResponseEntity.status(HttpStatus.OK).body(auctionBoard);
            } else {
                // 유효하지 않은 입찰 금액
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            // 경매 게시물이 없음
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 게시글 작성
    @PostMapping("/user/post")
    public ResponseEntity<AuctionBoard> create(@AuthenticationPrincipal String id, @RequestParam(name = "image", required = false) MultipartFile[] images, String title, String itemName, String desc, int sMoney, int eMoney, int gMoney, char nowBuy, String categoryNo) {
        AuctionBoard vo = new AuctionBoard();

        // 이미지 경로를 저장할 변수
        StringBuilder imagePaths = new StringBuilder();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            // 각 이미지 처리
            for (MultipartFile image : images) {
                String originalImage = image.getOriginalFilename();
                String realImage = originalImage.substring(originalImage.lastIndexOf("\\") + 1);
                String uuid = UUID.randomUUID().toString();
                String saveImage = uploadPath + File.separator + uuid + "_" + realImage;
                Path pathImage = Paths.get(saveImage);

                // 이미지를 서버에 저장
                image.transferTo(pathImage);

                // 이미지 경로를 imagePaths에 추가
                imagePaths.append(uuid).append("_").append(realImage).append(",");
            }

            // 마지막 쉼표 제거
            if (imagePaths.length() > 0) {
                imagePaths.deleteCharAt(imagePaths.length() - 1);
            }

            // 나머지 필드 설정
            vo.setAuctionTitle(title);
            vo.setItemName(itemName);
            vo.setItemDesc(desc);
            vo.setAuctionSMoney(sMoney);
            vo.setAuctionEMoney(eMoney);
            vo.setAuctionNowbuy(nowBuy);
            vo.setAuctionGMoney(gMoney);
//            vo.setBuyerId(buyerId);
//            vo.setBuyerPoint(buyerPoint);
            vo.setAuctionImg(imagePaths.toString());
            vo.setAuctionEnd('N');
            Category category = new Category();
            category.setCategoryNo(Integer.parseInt(categoryNo));

            vo.setCategory(category);

            vo.setAuctionDate(new Date());

            // AUCTION_END_DATE 설정 (AUCTION_DATE + 30일)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(vo.getAuctionDate());
            calendar.add(Calendar.DAY_OF_MONTH, 30); // 30일 추가
//            calendar.add(Calendar.MINUTE, 120); // 테스트
            vo.setAuctionEndDate(calendar.getTime());

            Member member = new Member();
            member.setId(id);
            vo.setMemberId(member);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // AuctionBoard 엔티티 저장
        AuctionBoard savedAuction = auctionBoardService.create(vo);

        return ResponseEntity.status(HttpStatus.OK).body(savedAuction);
    }

    @PostMapping("/public/search")
    public ResponseEntity<AuctionBoardDTO> Search(@RequestBody RequestDTO request) {
        int page = request.getPage();
        String keyword = request.getKeyword();
        Sort sort = Sort.by("auctionNo").descending();
        Pageable pageable = PageRequest.of(page-1,5,sort); // 한 페이지에 5개
        Page<AuctionBoard> list = null;
        if(keyword == null){
            list = auctionBoardService.showAll(pageable);
        }else{
            list = auctionBoardService.Search(keyword, pageable);
        }
        AuctionBoardDTO dto = new AuctionBoardDTO();
        dto.setContent(list.getContent());
        dto.setGetTotalPages(list.getTotalPages());
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    // 게시글 수정
    @PutMapping("/user/auction/update/{no}")
    public ResponseEntity<AuctionBoard> update(@PathVariable int no, @AuthenticationPrincipal String id, @RequestParam(name = "image", required = false) MultipartFile[] images, String title, String itemName, String desc, int sMoney, int eMoney, int gMoney, char nowBuy, String categoryNo) {
        // 사용자 인증 정보를 가져오고, 현재 로그인한 사용자의 아이디를 확인합니다.
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserId = id; // 현재 사용자의 아이디

        // 경매 게시글을 조회하여 게시글 작성자의 아이디를 확인합니다.
        AuctionBoard auctionBoard = auctionBoardService.show(no);
        String postUserId = auctionBoard.getMemberId().getId(); // 게시글 작성자의 아이디
        String[] imageList = auctionBoard.getAuctionImg().split(","); // 기존 이미지들

        // 현재 로그인한 사용자와 게시글 작성자를 비교하여 권한 확인
        if (!currentUserId.equals(postUserId)) {
            // 권한이 없는 경우, 403 Forbidden 응답을 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // 이미지 경로를 저장할 변수
        StringBuilder imagePaths = new StringBuilder();
        imagePaths.append(auctionBoard.getAuctionImg()).append(",");

        try {
            // 각 이미지 처리
            for (MultipartFile image : images) {

                if(!Arrays.asList(imageList).contains(image.getOriginalFilename())) {

                    String originalImage = image.getOriginalFilename();
                    String realImage = originalImage.substring(originalImage.lastIndexOf("\\") + 1);
                    String uuid = UUID.randomUUID().toString();
                    String saveImage = uploadPath + File.separator + uuid + "_" + realImage;
                    Path pathImage = Paths.get(saveImage);

                    // 이미지를 서버에 저장
                    image.transferTo(pathImage);

                    // 이미지 경로를 imagePaths에 추가
                    imagePaths.append(uuid).append("_").append(realImage).append(",");

                }
            }

            // 마지막 쉼표 제거
            if (imagePaths.length() > 0) {
                imagePaths.deleteCharAt(imagePaths.length() - 1);
            }

            // 업데이트된 필드 설정
            auctionBoard.setAuctionTitle(title);
            auctionBoard.setItemName(itemName);
            auctionBoard.setItemDesc(desc);
            auctionBoard.setAuctionSMoney(sMoney);
            auctionBoard.setAuctionEMoney(eMoney);
            auctionBoard.setAuctionNowbuy(nowBuy);
            auctionBoard.setAuctionGMoney(gMoney);
            auctionBoard.setAuctionImg(imagePaths.toString());

            Category category = new Category();
            category.setCategoryNo(Integer.parseInt(categoryNo));

            auctionBoard.setCategory(category);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 업데이트된 경매 게시물을 저장
        AuctionBoard updatedAuction = auctionBoardService.update(auctionBoard);

        if (updatedAuction != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedAuction);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 게시글 삭제
    @DeleteMapping("/user/auction/{no}")
    public ResponseEntity<AuctionBoard> delete(@PathVariable int no, @AuthenticationPrincipal String id) {
        // 사용자 인증 정보를 가져오고, 현재 로그인한 사용자의 아이디를 확인합니다.
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserId = id; // 현재 사용자의 아이디

        // 경매 게시글을 조회하여 게시글 작성자의 아이디를 확인합니다.
        AuctionBoard auctionBoard = auctionBoardService.show(no);
        String postUserId = auctionBoard.getMemberId().getId(); // 게시글 작성자의 아이디

        // 현재 로그인한 사용자와 게시글 작성자를 비교하여 권한 확인
        if (currentUserId.equals(postUserId)) {
            // 권한이 있는 경우, 게시글 삭제
            AuctionBoard deletedAuction = auctionBoardService.delete(no);
            if (deletedAuction != null) {
                return ResponseEntity.status(HttpStatus.OK).body(deletedAuction);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } else {
            // 권한이 없는 경우, 403 Forbidden 응답을 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    // 카테고리
    @GetMapping("/auction/{auctionNo}")
    public ResponseEntity<List<Category>> categoryList(@PathVariable int auctionNo){
        return ResponseEntity.status(HttpStatus.OK).body(category.findByAuctionNo(auctionNo));
    }

    // Hot 게시글
    @GetMapping("/public/auction/hot")
    public ResponseEntity<List<AuctionBoard>> HotList() {

        try {
            // 결과를 10개로 제한
            List<AuctionBoard> result = auctionBoardService.findByHot(10);
            log.info(""+result);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // New 게시글
    @GetMapping("/public/auction/new")
    public ResponseEntity<List<AuctionBoard>> NewList() {
        try {
            // 결과를 10개로 제한
            List<AuctionBoard> result = auctionBoardService.findByNew(10);
            log.info(""+result);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 사용자 총 게시물 조회
    @GetMapping("/public/auction/count")
    public ResponseEntity<Integer> countAuctionByMemberId(@RequestParam("memberId") String memberId) {
        Integer result = auctionBoardService.countAuctionByMemberId(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



}