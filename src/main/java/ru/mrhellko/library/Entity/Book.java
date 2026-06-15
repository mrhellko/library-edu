package ru.mrhellko.library.Entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Book implements IBook{

    private Long id;

    private String bookName;
    private List<Author> authors = new ArrayList<>();
    private List<Genre> genres = new ArrayList<>();
}
