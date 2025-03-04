package org.timowa.megabazar.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.timowa.megabazar.dto.UserReadDto;
import org.timowa.megabazar.dto.UserRegistrationDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    private UserService service;

    @Test
    void registrationTest() {
        UserRegistrationDto invalidUser =
                new UserRegistrationDto("oleg",
                        "oleg", // Invalid Email
                        ""); // Invalid password
        assertThrows(ConstraintViolationException.class,
                () -> service.registration(invalidUser));

        UserRegistrationDto user =
                new UserRegistrationDto("oleg",
                        "oleg@gmail.com",
                        "1234");
        UserReadDto resultUser = service.registration(user);

        assertNotNull(resultUser);
        assertEquals("oleg", resultUser.getUsername());
    }
}