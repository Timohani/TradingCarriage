package org.timowa.megabazar.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}