package ru.mrhellko.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrhellko.library.Entity.Book;
import ru.mrhellko.library.assembler.BookAssembler;
import ru.mrhellko.library.dto.BookWithAverageRatingDTO;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookAssembler bookAssembler;

    @GetMapping("/")
    public ResponseEntity<List<BookWithAverageRatingDTO>> getAll() {
        List<BookWithAverageRatingDTO> bookWithAverageRatingDTOs = bookAssembler.getFullAllBooks();
        if (!bookWithAverageRatingDTOs.isEmpty()) {
            return new ResponseEntity<>(bookWithAverageRatingDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookWithAverageRatingDTO> getBookById(@PathVariable Long id) {
        BookWithAverageRatingDTO bookWithAverageRatingDTO = bookAssembler.getFullBookWithAverageRatingDTO(id);
        if (bookWithAverageRatingDTO != null) {
            return new ResponseEntity<>(bookWithAverageRatingDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@RequestBody Book book, @PathVariable Long id) {
        Book updatedBook = bookAssembler.updateBook(book, id);
        if (updatedBook != null) {
            return new ResponseEntity<>(updatedBook, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<Book> saveBook(@RequestBody Book book) {
        try {
            Book savedBook = bookAssembler.saveBook(book);
            return new ResponseEntity<>(savedBook, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        try {
            bookAssembler.deleteBook(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<List<BookWithAverageRatingDTO>> getBooksByAuthorName(@RequestParam(value = "authorName") String authorName) {
        List<BookWithAverageRatingDTO> bookWithAverageRatingDTOs = bookAssembler.getBooksByAuthorName(authorName);
        if (!bookWithAverageRatingDTOs.isEmpty()) {
            return new ResponseEntity<>(bookWithAverageRatingDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
