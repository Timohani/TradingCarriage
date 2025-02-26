package org.timowa.megabazar.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}