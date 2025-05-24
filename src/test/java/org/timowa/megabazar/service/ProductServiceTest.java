package org.timowa.megabazar.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.Cart;
import org.timowa.megabazar.database.entity.Role;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.UserRepository;
import org.timowa.megabazar.dto.product.ProductCreateEditDto;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.exception.ProductAlreadyExistsException;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private ProductCreateEditDto testProductCreateEditDto;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testUser")
                .email("testUser@gmail.com")
                .password("password")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .cart(new Cart())
                .build();
        userRepository.save(testUser);

        testProductCreateEditDto = new ProductCreateEditDto(
                "Фен",
                "Хороший фен, всё высушит",
                39999.99,
                52,
                null,
                testUser.getUsername()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        testUser.getUsername(),
                        "password",
                        Collections.emptyList() // или реальные authorities
                ));
        SecurityContextHolder.setContext(context);
    }

    @Test
    void create_shouldThrowConstraintViolationException() {
        ProductCreateEditDto invalidDto = new ProductCreateEditDto(
                "",
                "Из китая",
                -12.6,
                0,
                null,
                testUser.getUsername()
        );
        assertThrows(ConstraintViolationException.class, () -> productService.create(invalidDto));
    }

    @Test
    void create_shouldCreateProduct() {
        ProductReadDto expected = productService.create(testProductCreateEditDto);
        assertEquals(testProductCreateEditDto.getName(), expected.getName());
        assertEquals("testUser", expected.getCreator());
    }

    @Test
    void create_shouldThrowAlreadyExistsException() {
        productService.create(testProductCreateEditDto);
        assertThrows(ProductAlreadyExistsException.class, () -> productService.create(testProductCreateEditDto));
    }

    @Test
    void addQuantity() {
        ProductReadDto savedDto = productService.create(testProductCreateEditDto);
        ProductReadDto updatedDto = productService.addQuantity(savedDto.getId(),27);
        assertEquals(79, updatedDto.getQuantity());
    }
}