package ru.mrhellko.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mrhellko.library.Entity.Book;

@Data
@NoArgsConstructor
public class BookWithAverageRatingDTO extends Book {
    private Float averageRating;

    public BookWithAverageRatingDTO(Book book) {
        setId(book.getId());
        setBookName(book.getBookName());
        setAuthors(book.getAuthors());
        setGenres(book.getGenres());
    }
}
