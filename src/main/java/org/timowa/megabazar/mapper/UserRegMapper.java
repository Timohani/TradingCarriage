package org.timowa.megabazar.mapper;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.dto.UserRegistrationDto;

@Component
public class UserRegMapper implements Mapper<UserRegistrationDto, User> {
    @Override
    public User map(UserRegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }
}
