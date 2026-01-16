package ru.mrhellko.library.Entity;

import lombok.Data;

@Data
public class Book {

    private Long id;

    private String bookName;
    private String author;
}
