package ru.mrhellko.library.Entity;

import lombok.Data;

@Data
public class BookReview {
    private Long id;
    private Long bookId;
    private Byte rating;

    private String reviewerName;
    private String text;
}
