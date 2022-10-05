package ru.practicum.shareit.review;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.review.model.Review;

import java.time.LocalDateTime;

@Component
public class ReviewMapper {

    public static Review toReview(ReviewDto reviewDto) {
        return new Review(reviewDto.getId(), reviewDto.getText(), null, null, LocalDateTime.now());
    }

    public static ReviewDto toReviewDto(Review review) {
        return new ReviewDto(review.getId(), review.getText(), review.getAuthor().getName(), review.getCreated());
    }
}
