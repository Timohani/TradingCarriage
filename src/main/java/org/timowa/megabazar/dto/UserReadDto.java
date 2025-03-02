package org.timowa.megabazar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserReadDto {
    private Long id;

    private String username;

    private String email;
}
