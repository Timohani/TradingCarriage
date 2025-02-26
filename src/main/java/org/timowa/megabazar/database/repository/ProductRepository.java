package org.timowa.megabazar.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}