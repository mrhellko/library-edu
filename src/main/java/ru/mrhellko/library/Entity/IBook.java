package ru.mrhellko.library.Entity;

import java.util.List;

public interface IBook {
    Long getId();
    List<Author> getAuthors();
    List<Genre> getGenres();
}
