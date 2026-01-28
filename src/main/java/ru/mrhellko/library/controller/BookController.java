package ru.mrhellko.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.assembler.BookAssembler;
import ru.mrhellko.library.dto.BookWithAverageRatingDTO;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookAssembler bookAssembler;

    @GetMapping("/")
    public ResponseEntity<List<BookWithAverageRatingDTO>> getAll() {
        return bookAssembler.getFullAllBooks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookWithAverageRatingDTO> getBookById(@PathVariable Long id) {
        return bookAssembler.getFullBookWithAverageRatingDTO(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@RequestBody Book book, @PathVariable Long id) {
        return bookAssembler.updateBook(book, id);
    }

    @PostMapping("/")
    public ResponseEntity<Book> saveBook(@RequestBody Book book) {
        return bookAssembler.saveBook(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        return bookAssembler.deleteBook(id);
    }

    @GetMapping("/by-author/{authorName}")
    public ResponseEntity<List<BookWithAverageRatingDTO>> getBooksByAuthorName(@PathVariable String authorName) {
        return bookAssembler.getBooksByAuthorName(authorName);
    }
}
