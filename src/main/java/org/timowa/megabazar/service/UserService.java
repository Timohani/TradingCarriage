package org.timowa.megabazar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.UserRepository;
import org.timowa.megabazar.dto.UserReadDto;
import org.timowa.megabazar.dto.UserRegistrationDto;
import org.timowa.megabazar.exception.UserAlreadyExistsException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserReadDto registration(UserRegistrationDto user) {
        log.info("Attempting to register user with email: {}", user.getEmail());
        Optional<User> checkUser = userRepository.findByEmailOrUsername(
                user.getEmail(),
                user.getUsername());
        if (checkUser.isPresent()) {
            throw new UserAlreadyExistsException("User is already exists");
        }
        User userToSave = new User();
        userToSave.setUsername(user.getUsername());
        userToSave.setEmail(user.getEmail());
        userToSave.setPassword(user.getPassword());
        userToSave.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(userToSave);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return new UserReadDto(savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail());
    }
}
