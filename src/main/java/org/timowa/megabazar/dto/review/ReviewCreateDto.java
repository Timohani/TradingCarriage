package org.timowa.megabazar.dto.review;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.timowa.megabazar.database.entity.Product;
import org.timowa.megabazar.database.entity.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewCreateDto {

    private User user;

    private Product product;

    @Size(min = 1, max = 5)
    private int rating;

    private String comment;

    private LocalDateTime createdAt;
}
