package ru.mrhellko.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrhellko.library.Entity.Author;
import ru.mrhellko.library.assembler.AuthorService;
import ru.mrhellko.library.exception.NotFoundException;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    @Autowired
    private AuthorService authorService;

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        Author author = authorService.getAuthorById(id);
        if (author != null) {
            return new ResponseEntity<>(author, HttpStatus.OK);
        } else {
            throw new NotFoundException(id);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@RequestBody Author author, @PathVariable Long id) {
        Author updatedAuthor = authorService.updateAuthor(author, id);
        if (updatedAuthor != null) {
            return new ResponseEntity<>(updatedAuthor, HttpStatus.OK);
        } else {
            throw new NotFoundException(id);
        }
    }

    @PostMapping("/")
    public ResponseEntity<Author> saveAuthor(@RequestBody Author author) {
        Author savedAuthor = authorService.saveAuthor(author);
        return new ResponseEntity<>(savedAuthor, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthorById(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
