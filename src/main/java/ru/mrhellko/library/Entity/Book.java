package ru.mrhellko.library.Entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Book {

    private Long id;

    private String bookName;
    private List<Author> authors = new ArrayList<>();
}
