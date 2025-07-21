package org.timowa.megabazar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.timowa.megabazar.dto.review.ReviewCreateDto;
import org.timowa.megabazar.dto.review.ReviewReadDto;
import org.timowa.megabazar.service.ReviewService;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("product/{productId}")
    public ResponseEntity<PagedModel<ReviewReadDto>> findAll(
            @PathVariable Long productId, Pageable pageable) {
        Page<ReviewReadDto> page = reviewService.findAllByProductId(pageable, productId);
        return ResponseEntity.ok()
                .body(new PagedModel<>(page));
    }

    @PostMapping("{productId}")
    public ResponseEntity<ReviewReadDto> createReview(
            @PathVariable Long productId, @RequestBody ReviewCreateDto createDto) {
        return ResponseEntity.ok()
                .body(reviewService.createReview(createDto, productId));
    }

    @GetMapping("{id}")
    public ReviewReadDto getOne(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteOne(Long id) {
        reviewService.delete(id);
        return ResponseEntity.ok().body(null);
    }
}
