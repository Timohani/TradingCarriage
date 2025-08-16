package org.timowa.megabazar.dto.orderItem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemReadDto {

    private Long id;

    private Long orderId;

    private Long productId;

    private Integer quantity;

    private Double price;
}
