package org.timowa.megabazar.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.Cart;
import org.timowa.megabazar.database.entity.Role;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.UserRepository;
import org.timowa.megabazar.dto.product.ProductCreateEditDto;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.exception.ProductAlreadyExistsException;
import org.timowa.megabazar.exception.ProductNotFoundException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;
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
    void deleteProduct_shouldDelete() {
        Long id = testCreatedProduct.getId();
        productService.delete(id);
        assertThrows(ProductNotFoundException.class, () -> productService.findById(id));
    }

    @Test
    void deleteProduct_shouldThrowException() {
        assertThrows(ProductNotFoundException.class, () -> productService.delete(993L));
    }
}