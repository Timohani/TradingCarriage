package org.timowa.megabazar.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.timowa.megabazar.dto.UserReadDto;
import org.timowa.megabazar.dto.UserRegistrationDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @MockitoBean
    private UserService service;

    @Test
    void registrationTest() {
        UserRegistrationDto userRegistrationDto =
                new UserRegistrationDto("gosha",
                        "oleg@gmail.com",
                        "amogus");
        UserReadDto user = service.registration(userRegistrationDto);
        assertEquals("gosha", user.getUsername());
    }
}