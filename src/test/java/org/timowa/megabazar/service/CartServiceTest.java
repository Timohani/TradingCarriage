package org.timowa.megabazar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.*;
import org.timowa.megabazar.database.repository.CartItemRepository;
import org.timowa.megabazar.database.repository.ProductRepository;
import org.timowa.megabazar.database.repository.UserRepository;
import org.timowa.megabazar.dto.cartItem.CartItemReadDto;
import org.timowa.megabazar.exception.CartLimitExceededException;
import org.timowa.megabazar.exception.ProductNotAvailableException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private LoginContext loginContext;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {

        // Создаем и сохраняем тестового пользователя
        testUser = User.builder()
                .username("testUser")
                .email("testUser@gmail.com")
                .password("password")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .cart(new Cart())
                .build();
        userRepository.save(testUser);

        // Создаем и сохраняем тестовый товар
        testProduct = Product.builder()
                .name("Test Product")
                .price(100.0)
                .quantity(10)
                .creator(testUser)
                .build();
        productRepository.save(testProduct);

        loginContext.setLoginUser(testUser);
    }

    @Test
    void addItemToCart_shouldAddNewItem() throws CartLimitExceededException, ProductNotAvailableException {
        // Act
        CartItemReadDto result = cartService.addItemToCart(testProduct.getId());

        // Assert
        assertNotNull(result.getId());
        assertEquals(1, result.getQuantity());
        assertEquals(testProduct.getId(), result.getProductId());
    }

    @Test
    void addItemToCart_shouldIncrementExistingItem() throws CartLimitExceededException, ProductNotAvailableException {
        // Arrange - сначала добавляем товар
        cartService.addItemToCart(testProduct.getId());

        // Act - добавляем тот же товар ещё раз
        CartItemReadDto result = cartService.addItemToCart(testProduct.getId());

        // Assert
        assertEquals(2, result.getQuantity());

        // Проверяем БД
        CartItem savedItem = cartItemRepository.findByCartAndProduct(
                testUser.getCart(),
                testProduct
        ).orElseThrow();
        assertEquals(2, savedItem.getQuantity());
    }

    @Test
    void addItemToCart_shouldThrowWhenProductUnavailable() {
        // Arrange
        testProduct.setQuantity(0);
        productRepository.save(testProduct);

        // Act & Assert
        assertThrows(ProductNotAvailableException.class,
                () -> cartService.addItemToCart(testProduct.getId()));
    }

    @Test
    void removeItemFromCart_shouldRemove1Quantity() throws CartLimitExceededException, ProductNotAvailableException {
        Product product = productRepository.save(testProduct);

        for (int i = 0; i < 3; i++) {
            cartService.addItemToCart(product.getId());
        }

        CartItemReadDto updatedItem = cartService.removeItemFromCart(product.getId());

        assertEquals(2, updatedItem.getQuantity());
    }

    @Test
    void removeItemFromCart_shouldRemoveItem() throws CartLimitExceededException, ProductNotAvailableException {
        Product product = productRepository.save(testProduct);
        cartService.addItemToCart(product.getId());

        cartService.removeItemFromCart(product.getId());

        Cart currentCart = cartService.getCurrentUserCart();
        assertFalse(cartItemRepository.findByCartAndProduct(currentCart, product).isPresent());
    }

    @Test
    void getCurrentUserCart_shouldReturnCorrectCart() {
        // Act
        Cart cart = cartService.getCurrentUserCart();

        // Assert
        assertEquals(testUser.getCart().getId(), cart.getId());
    }
}