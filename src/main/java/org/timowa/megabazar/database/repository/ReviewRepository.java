package org.timowa.megabazar.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}