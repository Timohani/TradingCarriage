package org.timowa.megabazar.mapper.user;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.dto.user.UserProfileDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
public class UserProfileMapper implements Mapper<User, UserProfileDto> {

    @Override
    public UserProfileDto map(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getMoney()
        );
    }
}
