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
import org.timowa.megabazar.exception.CartLimitExceededException;
import org.timowa.megabazar.exception.ProductNotAvailableException;
import org.timowa.megabazar.exception.ProductNotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public Cart getCurrentUserCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getCart();
    }

    @Transactional
    public CartItem addItemToCart(Long productId) throws ProductNotAvailableException, CartLimitExceededException {
        Cart currentCart = getCurrentUserCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        if (!product.isAvailable()) {
            throw new ProductNotAvailableException("Product is not available");
        }

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartAndProduct(currentCart, product);
        if (existingItem.isPresent() && existingItem.get().getQuantity() >= product.getQuantity()) {
            throw new CartLimitExceededException("Maximum quantity reached");
        }

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            return cartItemRepository.save(item);
        }

        CartItem newItem = CartItem.builder()
                .cart(currentCart)
                .product(product)
                .quantity(1)
                .build();

        return cartItemRepository.save(newItem);
    }
}
