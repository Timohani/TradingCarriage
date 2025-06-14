package org.timowa.megabazar.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.entity.Review;
import org.timowa.megabazar.database.entity.User;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserAndProduct(User user, Product product);
}