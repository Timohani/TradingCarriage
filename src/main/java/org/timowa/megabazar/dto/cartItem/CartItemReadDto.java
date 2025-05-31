package org.timowa.megabazar.dto.cartItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemReadDto {

    private Long id;

    private Long cartId;

    private Long productId;

    private int quantity;
}
