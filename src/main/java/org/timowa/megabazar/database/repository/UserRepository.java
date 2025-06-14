package org.timowa.megabazar.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.timowa.megabazar.database.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);
}
