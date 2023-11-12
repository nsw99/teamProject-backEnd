package com.kh.auction.controller;

import com.kh.auction.domain.Category;
import com.kh.auction.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins={"*"}, maxAge = 6000)
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/category")
    public ResponseEntity<List<Category>> showAll() {
        log.info("category : ");
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.showAll());
    }

    @GetMapping("/public/category/{no}")
    public ResponseEntity<Category> show(@PathVariable int no) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.show(no));
    }

    @PostMapping("/category")
    public ResponseEntity<Category> create(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.create(category));
    }

    @PutMapping("/category")
    public ResponseEntity<Category> update(@RequestBody Category category) {
        Category result = categoryService.update(category);
        if (result != null) {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }

    @DeleteMapping("/category/{no}")
    public ResponseEntity<Category> delete(@PathVariable int no) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.delete(no));
    }


}