package org.timowa.megabazar.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}