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
import org.timowa.megabazar.dto.user.UserProfileDto;
import org.timowa.megabazar.dto.user.UserReadDto;
import org.timowa.megabazar.dto.user.UserRegistrationDto;
import org.timowa.megabazar.exception.InsufficientFundsException;
import org.timowa.megabazar.exception.UserAlreadyExistsException;
import org.timowa.megabazar.exception.UserNotFoundException;
import org.timowa.megabazar.mapper.user.UserInfoMapper;
import org.timowa.megabazar.mapper.user.UserProfileMapper;
import org.timowa.megabazar.mapper.user.UserReadMapper;
import org.timowa.megabazar.mapper.user.UserRegMapper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

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
    private final UserProfileMapper userProfileMapper;

    private final Random random = new Random();

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

    public UserProfileDto getProfile(String username) {
        User user = getObjectByUsername(username);
        return userProfileMapper.map(user);
    }

    // Метод использует заглушку со случайным пополнением
    public UserProfileDto replenishBalance(String username, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Number must be more than 0");
        }
        // Настраиваемые параметры:
        double k = 0.001;      // Чем больше, тем резче падает вероятность
        int offset = 1000;     // Число, после которого вероятность резко снижается

        // Формула сигмоиды (вероятность падает с ростом числа)
        double probability = 1.0 / (1 + Math.exp(k * (count - offset)));

        boolean chance = random.nextDouble() < probability;
        if (chance) {
            User user = getObjectByUsername(username);
            user.setMoney(user.getMoney() + count);
            return userProfileMapper.map(user);
        }
        throw new InsufficientFundsException("Insufficient Funds");
    }

    public UserInfoDto getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        return userInfoMapper.map(user.get());
    }

    public User getObjectByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with username " + username + " not found");
        }
        return user.get();
    }

    public Page<UserInfoDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userInfoMapper::map);
    }

    public void deleteUser(Long id) {
        UserInfoDto user = getUserById(id);
        userRepository.deleteById(user.getId());
    }
}
