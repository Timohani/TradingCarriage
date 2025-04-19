package org.timowa.megabazar.mapper.user;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.dto.user.UserReadDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
public class UserReadMapper implements Mapper<User, UserReadDto> {
    @Override
    public UserReadDto map(User user) {
        return new UserReadDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
