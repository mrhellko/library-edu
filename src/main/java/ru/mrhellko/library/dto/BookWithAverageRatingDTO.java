package ru.mrhellko.library.dto;

import lombok.Data;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.Entity.Book;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookWithAverageRatingDTO {
    private Long id;

    private String bookName;
    private List<Author> authors;
    private Float averageRating;

    public BookWithAverageRatingDTO(Book book) {
        id = book.getId();
        bookName = book.getBookName();
        authors = book.getAuthors();
    }
}
