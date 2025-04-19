package org.timowa.megabazar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.timowa.megabazar.database.entity.Role;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private Long id;

    private String username;

    private String email;

    private Role role;

    private LocalDateTime createdAt;
}
