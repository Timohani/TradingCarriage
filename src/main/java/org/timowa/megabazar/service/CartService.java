package org.timowa.megabazar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.Cart;
import org.timowa.megabazar.database.entity.CartItem;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.CartItemRepository;
import org.timowa.megabazar.database.repository.CartRepository;
import org.timowa.megabazar.dto.cartItem.CartItemReadDto;
import org.timowa.megabazar.dto.product.ProductReadDto;
import org.timowa.megabazar.exception.CartItemNotFoundException;
import org.timowa.megabazar.exception.CartLimitExceededException;
import org.timowa.megabazar.exception.ProductNotAvailableException;
import org.timowa.megabazar.mapper.cartItem.CartItemReadMapper;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CartService {

    private final LoginContext loginContext;
    private final ProductService productService;

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    private final CartItemReadMapper cartItemReadMapper;

    public Cart getCurrentUserCart() {
        User user = loginContext.getLoginUser();
        Cart cart = user.getCart();
        if (cart == null) {
            Cart newCart = Cart.builder()
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();
            cartRepository.save(newCart);
            return newCart;
        }
        return cart;
    }

    public CartItemReadDto addItemToCart(Long productId) throws ProductNotAvailableException, CartLimitExceededException {
        Cart currentCart = getCurrentUserCart();

        ProductReadDto productDto = productService.findById(productId);
        if (!productDto.isAvailable()) {
            throw new ProductNotAvailableException("Product is not available");
        }

        Product product = productService.getObjectById(productDto.getId());
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartAndProduct(currentCart, product);
        if (existingItem.isPresent()) {
            if (existingItem.get().getQuantity() >= product.getQuantity()) {
                throw new CartLimitExceededException("Maximum quantity reached");
            }
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            return cartItemReadMapper.map(cartItemRepository.save(item));
        }

        CartItem newItem = CartItem.builder()
                .cart(currentCart)
                .product(product)
                .quantity(1)
                .build();

        return cartItemReadMapper.map(cartItemRepository.save(newItem));
    }

    public CartItemReadDto removeItemFromCart(Long productId) {
        Cart currentCart = getCurrentUserCart();
        Product product = productService.getObjectById(productId);

        CartItem item = cartItemRepository
                .findByCartAndProduct(currentCart, product)
                .orElseThrow(() -> new CartItemNotFoundException("CartItem not found"));

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            return cartItemReadMapper.map(item);
        }
        cartItemRepository.delete(item);
        return new CartItemReadDto();
    }
}
