package org.timowa.megabazar.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.timowa.megabazar.database.entity.OrderStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderPreviewDto {

    private Long id;

    private Double totalPrice;

    private OrderStatus status;

    private LocalDateTime createdAt;
}
