package org.timowa.megabazar.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.timowa.megabazar.database.entity.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewReadDto {

    private Long id;

    private User user;

    private Long productId;

    private int rating;

    private String comment;

    private LocalDateTime createdAt;
}
