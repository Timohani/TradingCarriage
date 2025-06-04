package org.timowa.megabazar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.Cart;
import org.timowa.megabazar.database.entity.CartItem;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.CartItemRepository;
import org.timowa.megabazar.database.repository.ProductRepository;
import org.timowa.megabazar.database.repository.UserRepository;
import org.timowa.megabazar.dto.cartItem.CartItemReadDto;
import org.timowa.megabazar.exception.CartItemNotFoundException;
import org.timowa.megabazar.exception.CartLimitExceededException;
import org.timowa.megabazar.exception.ProductNotAvailableException;
import org.timowa.megabazar.exception.ProductNotFoundException;
import org.timowa.megabazar.mapper.cartItem.CartItemReadMapper;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    private final CartItemReadMapper cartItemReadMapper;

    public Cart getCurrentUserCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getCart();
    }

    public CartItemReadDto addItemToCart(Long productId) throws ProductNotAvailableException, CartLimitExceededException {
        Cart currentCart = getCurrentUserCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        if (!product.isAvailable()) {
            throw new ProductNotAvailableException("Product is not available");
        }

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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

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
