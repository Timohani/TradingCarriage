package org.timowa.megabazar.mapper;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.dto.UserInfoDto;

@Component
public class UserInfoMapper implements Mapper<User, UserInfoDto> {
    @Override
    public UserInfoDto map(User user) {
        return new UserInfoDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
