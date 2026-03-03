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
            throw new NotFoundException(id);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@RequestBody Book book, @PathVariable Long id) throws Exception {
        Book updatedBook = bookAssembler.updateBook(book, id);
        if (updatedBook != null) {
            return new ResponseEntity<>(updatedBook, HttpStatus.OK);
        } else {
            throw new NotFoundException(id);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> saveBook(@RequestBody Book book) throws Exception {
        Book savedBook = bookAssembler.saveBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) throws Exception {
        bookAssembler.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookWithAverageRatingDTO>> getBooksByAuthorName(@RequestParam(value = "authorName") String authorName) {
        List<BookWithAverageRatingDTO> bookWithAverageRatingDTOs = bookAssembler.getBooksByAuthorName(authorName);
        if (!bookWithAverageRatingDTOs.isEmpty()) {
            return new ResponseEntity<>(bookWithAverageRatingDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<List<BookWithAverageRatingDTO>> getBooksByAuthorId(@PathVariable Long authorId) {
        List<BookWithAverageRatingDTO> bookWithAverageRatingDTOs = bookAssembler.getBooksByAuthorId(authorId);
        if (!bookWithAverageRatingDTOs.isEmpty()) {
            return new ResponseEntity<>(bookWithAverageRatingDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
