package org.timowa.megabazar.mapper.review;

import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.Review;
import org.timowa.megabazar.dto.review.ReviewReadDto;
import org.timowa.megabazar.mapper.Mapper;

@Component
public class ReviewReadMapper implements Mapper<Review, ReviewReadDto> {
    @Override
    public ReviewReadDto map(Review fromObject) {
        return new ReviewReadDto(
                fromObject.getId(),
                fromObject.getUser(),
                fromObject.getProduct().getId(),
                fromObject.getRating(),
                fromObject.getComment(),
                fromObject.getCreatedAt()
        );
    }
}
