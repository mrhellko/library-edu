package ru.mrhellko.library.dto;

import lombok.Data;
@Data
public class BookReviewByBookIdDTO {
    private String reviewerName;
    private String text;
    private Byte rating;
}
