package org.timowa.megabazar.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.dto.UserInfoDto;
import org.timowa.megabazar.dto.UserReadDto;
import org.timowa.megabazar.dto.UserRegistrationDto;
import org.timowa.megabazar.exception.UserAlreadyExistsException;
import org.timowa.megabazar.exception.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    private UserService service;

    @Test
    void registrationTest() {
        UserRegistrationDto invalidUser =
                new UserRegistrationDto(
                        "oleg",
                        "oleg", // Invalid Email
                        ""); // Invalid password
        assertThrows(ConstraintViolationException.class,
                () -> service.registration(invalidUser));

        UserRegistrationDto validUser =
                new UserRegistrationDto(
                        "oleg",
                        "oleg@gmail.com",
                        "1234");
        UserReadDto resultUser = service.registration(validUser);

        assertNotNull(resultUser);
        assertEquals(validUser.getUsername(), resultUser.getUsername());

        assertThrows(UserAlreadyExistsException.class,
                () -> service.registration(validUser));
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