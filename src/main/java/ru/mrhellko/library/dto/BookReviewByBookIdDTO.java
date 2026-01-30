package ru.mrhellko.library.dto;

import lombok.Data;
import ru.mrhellko.library.Entity.BookReview;

@Data
public class BookReviewByBookIdDTO {
    private String reviewerName;
    private String reviewText;
    private Byte rating;

    public BookReviewByBookIdDTO(BookReview bookReview) {
        reviewerName = bookReview.getReviewerName();
        reviewText = bookReview.getReviewText();
        rating = bookReview.getRating();
    }
}
