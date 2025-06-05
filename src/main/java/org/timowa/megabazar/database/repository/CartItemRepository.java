package org.timowa.megabazar.database.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.Cart;
import org.timowa.megabazar.database.entity.CartItem;
import org.timowa.megabazar.database.entity.Product;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    Page<CartItem> findAllByCartId(Long cartId, Pageable pageable);
}