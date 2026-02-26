package ru.mrhellko.library.dto;

import lombok.Data;

@Data
public class BookAuthorDTO {
    private Long bookId;
    private Long authorId;

    private String authorName;
}
