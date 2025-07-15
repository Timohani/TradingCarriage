package org.timowa.megabazar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.dto.user.UserReadDto;
import org.timowa.megabazar.dto.user.UserRegistrationDto;
import org.timowa.megabazar.mapper.user.UserReadMapper;
import org.timowa.megabazar.service.LoginContext;
import org.timowa.megabazar.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserReadMapper userReadMapper;
    private final LoginContext loginContext;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @PostMapping("/register")
    public ResponseEntity<UserReadDto> register(@Valid @RequestBody UserRegistrationDto dto) {
        UserReadDto createdDto = userService.registration(dto);
        loginContext.setLoginUser(userService.getUserByUsername(createdDto.getUsername()));
        if (loginContext.getLoginUser() == null) {
            throw new NullPointerException("Login user is null");
        }
        return ResponseEntity.ok(createdDto);
    }

    @GetMapping
    public UserReadDto getLoginUser() {
        User user = loginContext.getLoginUser();
        if (user == null) {
            throw new NullPointerException("Login user is null");
        }
        return userReadMapper.map(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.getUserByUsername(username);

        if (passwordEncoder.matches(password, user.getPassword())) {
            loginContext.setLoginUser(user);
        } else {
            throw new IllegalArgumentException("Password is incorrect");
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        loginContext.setLoginUser(null);
        return ResponseEntity.ok(null);
    }
}