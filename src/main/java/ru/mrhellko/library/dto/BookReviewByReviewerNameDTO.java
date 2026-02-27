package ru.mrhellko.library.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookReviewByReviewerNameDTO {
    private String reviewText;
    private Byte rating;
    private String bookName;
    private List<String> authorNames = new ArrayList<>();
}
