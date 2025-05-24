package org.timowa.megabazar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.dto.user.UserInfoDto;
import org.timowa.megabazar.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserInfoDto findById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/users")
    public Page<User> findAll(@RequestBody Pageable pageable) {
        return userService.findAll(pageable);
    }
}