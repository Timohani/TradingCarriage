package org.timowa.megabazar.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.entity.Review;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.ReviewRepository;
import org.timowa.megabazar.dto.review.ReviewCreateDto;
import org.timowa.megabazar.dto.review.ReviewReadDto;
import org.timowa.megabazar.exception.ReviewForThisProductAlreadyExistsException;
import org.timowa.megabazar.exception.ReviewNotFoundException;
import org.timowa.megabazar.exception.UserNotFoundException;
import org.timowa.megabazar.mapper.review.ReviewCreateMapper;
import org.timowa.megabazar.mapper.review.ReviewReadMapper;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final UserService userService;
    private final ProductService productService;

    private final ReviewCreateMapper reviewCreateMapper;
    private final ReviewReadMapper reviewReadMapper;

    public ReviewReadDto createReview(@Valid ReviewCreateDto createDto, Long productId) {
        Product product = productService.getObjectById(productId);

        if (reviewRepository.findByUserAndProduct(createDto.getUser(), product).isPresent()) {
            throw new ReviewForThisProductAlreadyExistsException("Review for product with id: "
                    + productId + " already exists from this user");
        }
        User user = userService.getLoginUser();
        Review savedReview = reviewRepository.save(reviewCreateMapper.map(createDto, user));
        return reviewReadMapper.map(savedReview);
    }

    public ReviewReadDto findById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review with id " + id + " not found"));
        return reviewReadMapper.map(review);
    }

    public Page<ReviewReadDto> findAll(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(reviewReadMapper::map);
    }

    public void delete(Long id) {
        Long userId = userService.getLoginUser().getId();
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review with id " + id + " not found"));
        if (review.getUser().getId().equals(userId)) {
            reviewRepository.delete(review);
            return;
        }
        throw new UserNotFoundException("User not found");
    }
}
