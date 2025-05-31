package org.timowa.megabazar.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.entity.User;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    User findCreator(Long productId);
}