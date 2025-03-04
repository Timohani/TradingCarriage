package org.timowa.megabazar.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.UserRepository;
import org.timowa.megabazar.dto.UserReadDto;
import org.timowa.megabazar.dto.UserRegistrationDto;
import org.timowa.megabazar.dto.mapper.UserReadMapper;
import org.timowa.megabazar.dto.mapper.UserRegMapper;
import org.timowa.megabazar.exception.UserAlreadyExistsException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@Validated
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserRegMapper userRegMapper;
    private final UserReadMapper userReadMapper;

    public UserReadDto registration(@Valid UserRegistrationDto userRegDto) {
        log.info("Attempting to register user with email: {}", userRegDto.getEmail());
        Optional<User> checkUser = userRepository.findByEmailOrUsername(
                userRegDto.getEmail(),
                userRegDto.getUsername());
        if (checkUser.isPresent()) {
            throw new UserAlreadyExistsException("User is already exists");
        }
        User user = userRegMapper.map(userRegDto);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return userReadMapper.map(savedUser);
    }
}
