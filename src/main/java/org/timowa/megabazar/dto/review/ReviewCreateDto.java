package org.timowa.megabazar.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewCreateDto {

    private Long userId;

    private int rating;

    private String comment;
}
