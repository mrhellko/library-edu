package ru.mrhellko.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrhellko.library.Entity.Genre;
import ru.mrhellko.library.assembler.GenreService;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    @Autowired
    private GenreService genreService;

    @GetMapping("/")
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genreList = genreService.getAllGenres();
        if (!genreList.isEmpty()) {
            return new ResponseEntity<>(genreList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable Long id) {
        Genre genre = genreService.getGenreById(id);
        if (genre != null) {
            return new ResponseEntity<>(genre, HttpStatus.OK);
        } else {
            throw new NotFoundException(id);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@RequestBody Genre genre, @PathVariable Long id) {
        Genre updatedGenre = genreService.updateGenre(genre, id);
        if (updatedGenre != null) {
            return new ResponseEntity<>(updatedGenre, HttpStatus.OK);
        } else {
            throw new NotFoundException(id);
        }
    }

    @PostMapping("/")
    public ResponseEntity<Genre> saveGenre(@RequestBody Genre genre) {
        Genre savedGenre = genreService.saveGenre(genre);
        return new ResponseEntity<>(savedGenre, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenreById(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
