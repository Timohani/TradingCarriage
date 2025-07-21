package org.timowa.megabazar.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<UserReadDto> register(@Valid @RequestBody UserRegistrationDto dto, HttpServletResponse response) {
        UserReadDto createdDto = userService.registration(dto);
        loginContext.setLoginUser(userService.getUserByUsername(createdDto.getUsername()));
        setLoginCookie(response, createdDto.getUsername());
        if (loginContext.getLoginUser() == null) {
            throw new NullPointerException("Login user is null");
        }
        return ResponseEntity.ok(createdDto);
    }

    @GetMapping
    public UserReadDto getLoginUser(HttpServletRequest request) {
        // Set login user from cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieValue = cookie.getValue();
                if ("loginUser".equals(cookie.getName()) && cookieValue != null) {
                    System.out.println("Username from cookie: " + cookieValue);
                    User userFromCookie = userService.getUserByUsername(cookieValue);
                    loginContext.setLoginUser(userFromCookie);
                }
            }
        }

        User user = loginContext.getLoginUser();
        if (user == null) {
            throw new NullPointerException("Login user is null");
        }
        return userReadMapper.map(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        User user = userService.getUserByUsername(username);

        if (passwordEncoder.matches(password, user.getPassword())) {
            loginContext.setLoginUser(user);
            setLoginCookie(response, username);
        } else {
            throw new IllegalArgumentException("Password is incorrect");
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        loginContext.setLoginUser(null);
        Cookie cookie = new Cookie("loginUser", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok(null);
    }

    private void setLoginCookie(HttpServletResponse response, String username) {
        Cookie cookie = new Cookie("loginUser", username);

        cookie.setMaxAge(7 * 24 * 60 * 60); // lifetime - 7 days
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}