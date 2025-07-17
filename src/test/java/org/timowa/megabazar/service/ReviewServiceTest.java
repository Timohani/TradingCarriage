package org.timowa.megabazar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.entity.Role;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.ProductRepository;
import org.timowa.megabazar.database.repository.ReviewRepository;
import org.timowa.megabazar.database.repository.UserRepository;
import org.timowa.megabazar.dto.review.ReviewCreateDto;
import org.timowa.megabazar.dto.review.ReviewReadDto;
import org.timowa.megabazar.exception.ReviewForThisProductAlreadyExistsException;
import org.timowa.megabazar.exception.ReviewNotFoundException;
import org.timowa.megabazar.exception.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private LoginContext loginContext;

    private Product testProduct;
    private ReviewCreateDto reviewCreateDto;

    private final long reviewInvalidId = 993L;

    @BeforeEach
    void setUp() {
        User userToSave = User.builder()
                .username("testtest")
                .password("fi9wh4f78hew78784wteshew")
                .email("testtest@test.test")
                .role(Role.USER)
                .build();
        User testUser = userRepository.save(userToSave);


        Product productToSave = Product.builder()
                .name("product")
                .description("mega product")
                .price(9999)
                .quantity(1234)
                .build();
        testProduct = productRepository.save(productToSave);

        loginContext.setLoginUser(testUser);

        reviewCreateDto = new ReviewCreateDto(5, "nice");
    }

    @Test
    void createReview() {
        ReviewReadDto reviewReadDto = reviewService.createReview(reviewCreateDto, testProduct.getId());
        assertNotNull(reviewRepository.findById(reviewReadDto.getId()));
    }

    @Test
    void createReview_shouldFail_whenInvalidRate() {
        ReviewCreateDto reviewInvalidCreateDto = new ReviewCreateDto(7, "nice");
        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(reviewInvalidCreateDto, testProduct.getId()));
    }

    @Test
    void createReview_shouldFail_whenReviewAlreadyExists() {
        reviewService.createReview(reviewCreateDto, testProduct.getId());
        assertThrows(ReviewForThisProductAlreadyExistsException.class, () -> reviewService.createReview(reviewCreateDto, testProduct.getId()));
    }

    @Test
    void findById() {
        ReviewReadDto readCreateDto = reviewService.createReview(reviewCreateDto, testProduct.getId());
        ReviewReadDto readFindDto = reviewService.findById(readCreateDto.getId());
        assertNotNull(readFindDto);
    }

    @Test
    void findById_shouldFail_whenNotFound() {
        assertThrows(ReviewNotFoundException.class, () -> reviewService.findById(reviewInvalidId));
    }

    @Test
    void findAllByProductId() {
        reviewService.createReview(reviewCreateDto, testProduct.getId());

        changeLoginContext();
        ReviewCreateDto reviewCreateDto1 = new ReviewCreateDto(3, "good");
        reviewService.createReview(reviewCreateDto1, testProduct.getId());

        Pageable pageable = PageRequest.ofSize(10);
        Page<ReviewReadDto> page = reviewService.findAllByProductId(pageable, testProduct.getId());
        assertEquals(2L, page.getTotalElements());
    }

    @Test
    void delete() {
        ReviewReadDto readDto = reviewService.createReview(reviewCreateDto, testProduct.getId());
        reviewService.delete(readDto.getId());
        assertThrows(ReviewNotFoundException.class, () -> reviewService.findById(readDto.getId()));
    }

    @Test
    void delete_shouldFail_whenReviewNotFound() {
        assertThrows(ReviewNotFoundException.class, () -> reviewService.delete(reviewInvalidId));
    }

    @Test
    void delete_shouldFail_whenIsNotAuthenticated() {
        ReviewCreateDto reviewCreateDtoWithOtherUser = new ReviewCreateDto(5, "nice");
        ReviewReadDto readDto = reviewService.createReview(reviewCreateDtoWithOtherUser, testProduct.getId());

        changeLoginContext();

        assertThrows(UserNotFoundException.class, () -> reviewService.delete(readDto.getId()));
    }

    void changeLoginContext() {
        User userToSave = User.builder()
                .username("tessst")
                .password("12345236")
                .email("teeeeestttt@tessst.teest")
                .role(Role.USER)
                .build();
        User testLoginUser = userRepository.save(userToSave);
        loginContext.setLoginUser(testLoginUser);
    }
}
