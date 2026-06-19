package ru.mrhellko.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.Entity.Genre;
import ru.mrhellko.library.Entity.IBook;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BookWithAverageRatingDTO implements IBook {
    private Long id;
    private Float averageRating;

    private String bookName;
    private List<Author> authors = new ArrayList<>();
    private List<Genre> genres = new ArrayList<>();

    public BookWithAverageRatingDTO(Book book) {
        setId(book.getId());
        setBookName(book.getBookName());
        setAuthors(book.getAuthors());
        setGenres(book.getGenres());
    }
}
