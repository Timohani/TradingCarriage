package org.timowa.megabazar.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.timowa.megabazar.database.entity.Role;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserProfileDto {
    private Long id;

    private String username;

    private String email;

    private Role role;

    private LocalDateTime createdAt;

    private Integer money;
}
