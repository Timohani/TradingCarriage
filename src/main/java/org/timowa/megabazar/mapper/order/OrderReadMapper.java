package org.timowa.megabazar.mapper.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.Order;
import org.timowa.megabazar.dto.order.OrderReadDto;
import org.timowa.megabazar.mapper.Mapper;
import org.timowa.megabazar.mapper.orderItems.OrderItemReadMapper;

@Component
@RequiredArgsConstructor
public class OrderReadMapper implements Mapper<Order, OrderReadDto> {

    private final OrderItemReadMapper orderItemReadMapper;

    @Override
    public OrderReadDto map(Order order) {
        return new OrderReadDto(
                order.getId(),
                order.getUser().getUsername(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getOrderItems().stream().map(orderItemReadMapper::map).toList()
        );
    }
}
