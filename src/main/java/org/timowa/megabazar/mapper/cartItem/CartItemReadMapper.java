package org.timowa.megabazar.mapper.cartItem;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.CartItem;
import org.timowa.megabazar.dto.cartItem.CartItemReadDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
public class CartItemReadMapper implements Mapper<CartItem, CartItemReadDto> {
    @Override
    public CartItemReadDto map(CartItem fromObject) {
        return new CartItemReadDto(
                fromObject.getId(),
                fromObject.getCart().getId(),
                fromObject.getProduct().getId(),
                fromObject.getQuantity()
        );
    }
}
