package org.timowa.megabazar.mapper.orderItems;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.OrderItem;
import org.timowa.megabazar.dto.orderItem.OrderItemReadDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
public class OrderItemReadMapper implements Mapper<OrderItem, OrderItemReadDto> {
    @Override
    public OrderItemReadDto map(OrderItem orderItem) {
        return new OrderItemReadDto(
                orderItem.getId(),
                orderItem.getOrder().getId(),
                orderItem.getProduct().getId(),
                orderItem.getQuantity(),
                orderItem.getPrice()
        );
    }
}
