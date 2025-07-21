package org.timowa.megabazar.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.timowa.megabazar.database.entity.Cart;
import org.timowa.megabazar.database.entity.CartItem;
import org.timowa.megabazar.database.repository.CartItemRepository;
import org.timowa.megabazar.dto.cartItem.CartItemReadDto;
import org.timowa.megabazar.exception.CartLimitExceededException;
import org.timowa.megabazar.exception.ProductNotAvailableException;
import org.timowa.megabazar.mapper.cartItem.CartItemReadMapper;
import org.timowa.megabazar.service.CartService;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Transactional
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final CartService cartService;

    private final CartItemReadMapper cartItemReadMapper;

    @GetMapping
    public PagedModel<CartItemReadDto> getCart(Pageable pageable) {
        Cart cart = cartService.getCurrentUserCart();
        Page<CartItemReadDto> cartItems = cartItemRepository.findAllByCartId(cart.getId(), pageable).map(cartItemReadMapper::map);
        return new PagedModel<>(cartItems);
    }

    @GetMapping("/{id}")
    public CartItemReadDto getOneById(@PathVariable Long id) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        return cartItemOptional.map(cartItemReadMapper::map).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @PostMapping("/{productId}")
    public CartItemReadDto addItemToCart(@PathVariable Long productId) throws CartLimitExceededException, ProductNotAvailableException {
        return cartService.addItemToCart(productId);
    }

    @DeleteMapping("/{productId}")
    public CartItemReadDto removeItemFromCart(@PathVariable Long productId) {
        return cartService.removeItemFromCart(productId);
    }
}
