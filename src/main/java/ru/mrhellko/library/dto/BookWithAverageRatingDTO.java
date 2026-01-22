package ru.mrhellko.library.dto;

import lombok.Data;

@Data
public class BookWithAverageRatingDTO {
    private Long id;

    private String bookName;
    private String author;
    private Float averageRating;
}
