package org.timowa.megabazar.service;

import jakarta.validation.ConstraintViolationException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.Cart;
import org.timowa.megabazar.database.entity.Role;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.ProductRepository;
import org.timowa.megabazar.database.repository.UserRepository;
import org.timowa.megabazar.dto.product.ProductCreateEditDto;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.exception.ProductAlreadyExistsException;
import org.timowa.megabazar.exception.ProductNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LoginContext loginContext;
    @Autowired
    private UserRepository userRepository;

    private ProductCreateEditDto testProductCreateEditDto;
    private ProductReadDto testCreatedProduct;

    @BeforeEach
    void setUp() {
        User testUser = User.builder()
                .username("testUser")
                .email("testUser@gmail.com")
                .password("password")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .cart(new Cart())
                .build();
        userRepository.save(testUser);

        loginContext.setLoginUser(testUser);

        testProductCreateEditDto = new ProductCreateEditDto(
                "Фен",
                "Хороший фен, всё высушит",
                39999.99,
                52
        );
        testCreatedProduct = productService.create(testProductCreateEditDto);
    }

    @Test
    void create_shouldThrowConstraintViolationException() {
        ProductCreateEditDto invalidDto = new ProductCreateEditDto(
                "",
                "Из китая",
                -12.6,
                0
        );
        assertThrows(ConstraintViolationException.class, () -> productService.create(invalidDto));
    }

    @Test
    void create_shouldThrowAlreadyExistsException() {
        assertThrows(ProductAlreadyExistsException.class, () -> productService.create(testProductCreateEditDto));
    }

    @Test
    void addQuantity() {
        ProductReadDto updatedDto = productService.addQuantity(testCreatedProduct.getId(),27);
        assertEquals(79, updatedDto.getQuantity());
    }

    @Test
    void delete_ShouldRemoveProduct_WhenUserIsCreator() throws AuthenticationException {
        Long productId = testCreatedProduct.getId();

        productService.delete(productId);

        assertFalse(productRepository.existsById(productId));
    }

    @Test
    void delete_ShouldThrowAuthenticationException_WhenUserIsNotCreator() {
        // Arrange
        Long productId = testCreatedProduct.getId();

        User anotherUser = User.builder()
                .username("anotherUser")
                .email("another@gmail.com")
                .password("password")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .cart(new Cart())
                .build();
        userRepository.save(anotherUser);
        loginContext.setLoginUser(anotherUser);

        assertThrows(AuthenticationException.class, () -> productService.delete(productId));
        assertTrue(productRepository.existsById(productId)); // Продукт не должен быть удален
    }

    @Test
    void delete_ShouldThrowEntityNotFoundException_WhenProductNotExists() {
        Long nonExistentId = 999L;

        assertThrows(ProductNotFoundException.class, () -> productService.delete(nonExistentId));
    }

    @Test
    void deleteMany_ShouldDeleteAllProducts_WhenUserIsCreator() throws AuthenticationException {
        testProductCreateEditDto.setName("Name");
        ProductReadDto secondProduct = productService.create(testProductCreateEditDto);
        List<Long> ids = List.of(testCreatedProduct.getId(), secondProduct.getId());

        productService.deleteMany(ids);

        assertFalse(productRepository.findById(testCreatedProduct.getId()).isPresent());
        assertFalse(productRepository.findById(secondProduct.getId()).isPresent());
    }

    @Test
    void deleteMany_ShouldDeleteOnlyProductsUserIsCreatorOf() {
        User anotherUser = User.builder()
                .username("anotherUser")
                .email("another@gmail.com")
                .password("password")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .cart(new Cart())
                .build();
        userRepository.save(anotherUser);

        loginContext.setLoginUser(anotherUser);
        testProductCreateEditDto.setName("Name");
        ProductReadDto anotherProduct = productService.create(testProductCreateEditDto);

        User originalUser = userRepository.findByUsername("testUser").orElseThrow();
        loginContext.setLoginUser(originalUser);

        List<Long> ids = List.of(testCreatedProduct.getId(), anotherProduct.getId());

        assertThrows(AuthenticationException.class, () -> productService.deleteMany(ids));

        assertFalse(productRepository.existsById(testCreatedProduct.getId())); // Удален
        assertTrue(productRepository.existsById(anotherProduct.getId())); // Не удален
    }

    @Test
    void deleteMany_ShouldNotThrow_WhenEmptyListProvided() {
        assertDoesNotThrow(() -> productService.deleteMany(List.of()));
    }
}