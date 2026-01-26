package ru.mrhellko.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrhellko.library.assembler.BookAssembler;
import ru.mrhellko.library.dto.BookWithAverageRatingDTO;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookAssembler bookAssembler;

    @GetMapping("/")
    public List<BookWithAverageRatingDTO> getAll() {
        return bookAssembler.getFullAllBooks();
    }

    @GetMapping("/{id}")
    public BookWithAverageRatingDTO getBookById(@PathVariable Long id) {
        return bookAssembler.getFullBookWithAverageRatingDTO(id);
    }
}
