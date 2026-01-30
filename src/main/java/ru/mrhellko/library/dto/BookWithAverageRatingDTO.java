package ru.mrhellko.library.dto;

import lombok.Data;
import ru.mrhellko.library.Entity.Book;

@Data
public class BookWithAverageRatingDTO {
    private Long id;

    private String bookName;
    private String author;
    private Float averageRating;

    public BookWithAverageRatingDTO(Book book) {
        id = book.getId();
        bookName = book.getBookName();
        author = book.getAuthor();
    }
}
