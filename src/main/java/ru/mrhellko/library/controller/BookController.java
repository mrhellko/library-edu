package ru.mrhellko.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.dao.BookDAO;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookDAO bookDAO;

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookDAO.getBookById(id);
    }
}
