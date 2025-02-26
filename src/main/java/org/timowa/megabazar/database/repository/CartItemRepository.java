package org.timowa.megabazar.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}