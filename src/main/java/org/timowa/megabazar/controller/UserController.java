package org.timowa.megabazar.controller;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.timowa.megabazar.dto.PageDto;
import org.timowa.megabazar.dto.user.UserInfoDto;
import org.timowa.megabazar.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/{id}")
    public UserInfoDto findById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public PageDto<UserInfoDto> getAllUsers(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            @ParameterObject Pageable pageable) {

        Page<UserInfoDto> userPage = userService.findAll(pageable);
        return convertToPageDto(userPage);
    }

    private PageDto<UserInfoDto> convertToPageDto(Page<UserInfoDto> page) {
        return new PageDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}