package org.timowa.megabazar.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
