package org.timowa.megabazar.mapper.review;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.Review;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.dto.review.ReviewCreateDto;
import org.timowa.megabazar.mapper.Mapper;

import java.time.LocalDateTime;

@Component
public class ReviewCreateMapper implements Mapper<ReviewCreateDto, Review> {
    @Override
    public Review map(ReviewCreateDto fromObject) {
        Review review = new Review();
        review.setProduct(fromObject.getProduct());
        review.setRating(fromObject.getRating());
        review.setComment(fromObject.getComment());
        review.setCreatedAt(LocalDateTime.now());
        return null;
    }

    public Review map(ReviewCreateDto fromObject, User user) {
        Review review = new Review();
        review.setUser(user);
        review.setProduct(fromObject.getProduct());
        review.setRating(fromObject.getRating());
        review.setComment(fromObject.getComment());
        review.setCreatedAt(LocalDateTime.now());
        return null;
    }
}
