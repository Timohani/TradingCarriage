package org.timowa.megabazar.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.timowa.megabazar.database.entity.OrderStatus;
import org.timowa.megabazar.dto.orderItem.OrderItemReadDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderReadDto {

    private Long id;

    private String userName;

    private Double totalPrice;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private List<OrderItemReadDto> orderItems;
}
