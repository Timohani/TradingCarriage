package org.timowa.megabazar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.timowa.megabazar.dto.user.UserInfoDto;
import org.timowa.megabazar.dto.user.UserProfileDto;
import org.timowa.megabazar.exception.InsufficientFundsException;
import org.timowa.megabazar.exception.UserNotFoundException;
import org.timowa.megabazar.service.LoginContext;
import org.timowa.megabazar.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LoginContext loginContext;

    @GetMapping("/user/{id}")
    public UserInfoDto findById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public PagedModel<UserInfoDto> getAllUsers(Pageable pageable) {
        Page<UserInfoDto> userPage = userService.findAll(pageable);
        return new PagedModel<>(userPage);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile() {
        try {
            return ResponseEntity.ok(userService.getProfile(loginContext.getLoginUser().getUsername()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/replenishBalance")
    public ResponseEntity<?> replenishBalance(@RequestParam Integer count) {
        try {
            return ResponseEntity.ok(userService.replenishBalance(loginContext.getLoginUser().getUsername(), count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(418).body(e.getMessage());
        }
    }
}