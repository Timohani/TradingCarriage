package org.timowa.megabazar.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.database.repository.UserRepository;
import org.timowa.megabazar.dto.user.UserInfoDto;
import org.timowa.megabazar.dto.user.UserReadDto;
import org.timowa.megabazar.dto.user.UserRegistrationDto;
import org.timowa.megabazar.exception.UserAlreadyExistsException;
import org.timowa.megabazar.exception.UserNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registration_shouldFail_whenInvalidDataProvided() {
        // Given
        UserRegistrationDto invalidUser = new UserRegistrationDto(
                "oleg",
                "oleg",  // Invalid Email
                ""      // Invalid password
        );

        // When & Then
        assertThrows(ConstraintViolationException.class,
                () -> service.registration(invalidUser));
    }

    @Test
    void registration_shouldSuccess_whenValidDataProvided() {
        // Given
        UserRegistrationDto validUser = new UserRegistrationDto(
                "oleg",
                "oleg@gmail.com",
                "1234"
        );

        // When
        UserReadDto resultUser = service.registration(validUser);

        // Then
        assertNotNull(resultUser, "Returned user DTO should not be null");
        assertEquals(validUser.getUsername(), resultUser.getUsername(),
                "Username should match input");

        Optional<User> savedUser = userRepository.findById(resultUser.getId());
        assertTrue(savedUser.isPresent(), "User should be saved in database");

        User userEntity = savedUser.get();
        assertNotNull(userEntity.getCart(), "User should have cart created");
        assertEquals(resultUser.getId(), userEntity.getCart().getUser().getId(),
                "Cart should reference correct user");
    }

    @Test
    void registration_shouldFail_whenUserAlreadyExists() {
        // Given
        UserRegistrationDto validUser = new UserRegistrationDto(
                "existingUser",
                "existing@gmail.com",
                "1234"
        );

        // First registration (should succeed)
        service.registration(validUser);

        // When & Then
        assertThrows(UserAlreadyExistsException.class,
                () -> service.registration(validUser),
                "Should not allow duplicate registration");
    }

    @Test
    void getTest() {
        assertThrows(UserNotFoundException.class,
                () -> service.getUser(993L));

        UserRegistrationDto userToSave = new UserRegistrationDto(
                "pavel",
                "pavel229@mail.ru",
                "22882");
        UserReadDto user = service.registration(userToSave);
        UserInfoDto findUser = service.getUser(user.getId());
        assertEquals(user.getUsername(), findUser.getUsername());
    }
}