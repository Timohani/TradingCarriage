package org.timowa.megabazar.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewReadDto {

    private Long id;

    private Long userId;

    private Long productId;

    private int rating;

    private String comment;

    private LocalDateTime createdAt;
}
