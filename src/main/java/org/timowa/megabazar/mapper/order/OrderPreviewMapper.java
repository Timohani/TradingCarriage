package org.timowa.megabazar.mapper.order;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.Order;
import org.timowa.megabazar.dto.order.OrderPreviewDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
public class OrderPreviewMapper implements Mapper<Order, OrderPreviewDto> {
    @Override
    public OrderPreviewDto map(Order order) {
        return new OrderPreviewDto(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
