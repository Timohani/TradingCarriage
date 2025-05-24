package org.timowa.megabazar.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.timowa.megabazar.database.entity.Cart;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.UserRepository;
import org.timowa.megabazar.dto.user.UserInfoDto;
import org.timowa.megabazar.dto.user.UserReadDto;
import org.timowa.megabazar.dto.user.UserRegistrationDto;
import org.timowa.megabazar.exception.UserAlreadyExistsException;
import org.timowa.megabazar.exception.UserNotFoundException;
import org.timowa.megabazar.mapper.user.UserInfoMapper;
import org.timowa.megabazar.mapper.user.UserReadMapper;
import org.timowa.megabazar.mapper.user.UserRegMapper;

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
    private final UserInfoMapper userInfoMapper;

    public UserReadDto registration(@Valid UserRegistrationDto userRegDto) {
        log.info("Attempting to register user with email: {}", userRegDto.getEmail());

        if (userRepository.existsByUsernameOrEmail(userRegDto.getUsername(), userRegDto.getEmail())) {
            throw new UserAlreadyExistsException("User is already exists");
        }
        User user = userRegMapper.map(userRegDto);

        Cart cart = new Cart();
        cart.setCreatedAt(LocalDateTime.now());
        user.setCart(cart);
        cart.setUser(user);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return userReadMapper.map(savedUser);
    }

    public UserInfoDto getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        return userInfoMapper.map(user.get());
    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with username " + username + " not found");
        }
        return user.get();
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
