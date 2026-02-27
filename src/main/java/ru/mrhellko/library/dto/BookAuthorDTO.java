package ru.mrhellko.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookAuthorDTO {
    private Long bookId;
    private Long authorId;

    private String authorName;
}
