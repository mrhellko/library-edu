package ru.mrhellko.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookGenreDTO {
    private Long bookId;
    private Long genreId;

    private String genreName;
}
