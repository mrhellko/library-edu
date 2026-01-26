package ru.mrhellko.library.dto;

import lombok.Data;

@Data
public class BookReviewByReviewerNameDTO {
    private String text;
    private Byte rating;
    private String bookName;
    private String author;
}
