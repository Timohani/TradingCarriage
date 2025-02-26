package org.timowa.megabazar.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}