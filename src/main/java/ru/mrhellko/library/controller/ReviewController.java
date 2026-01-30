package ru.mrhellko.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.assembler.BookReviewAssembler;
import ru.mrhellko.library.dto.BookReviewByBookIdDTO;
import ru.mrhellko.library.dto.BookReviewByReviewerNameDTO;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private BookReviewAssembler bookReviewAssembler;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookReviewByBookIdDTO>> getReviewByBookId(@PathVariable Long bookId) {
        List<BookReviewByBookIdDTO> bookReviewByBookIdDTOs = bookReviewAssembler.getReviewByBookId(bookId);
        if (!bookReviewByBookIdDTOs.isEmpty()) {
            return new ResponseEntity<>(bookReviewByBookIdDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping
    public ResponseEntity<List<BookReviewByReviewerNameDTO>> getReviewByReviewerName(@RequestParam(value = "reviewerName") String reviewerName) {
        List<BookReviewByReviewerNameDTO> bookReviewByReviewerNameDTOs = bookReviewAssembler.getReviewByReviewerName(reviewerName);
        if (!bookReviewByReviewerNameDTOs.isEmpty()) {
            return new ResponseEntity<>(bookReviewByReviewerNameDTOs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookReview> updateBookReview(@RequestBody BookReview bookReview, @PathVariable Long id) {
        BookReview updatedBookReview = bookReviewAssembler.updateBookReview(bookReview, id);
        if (updatedBookReview != null) {
            return new ResponseEntity<>(updatedBookReview, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<BookReview> saveBookReview(@RequestBody BookReview bookReview) {
        try {
            BookReview savedBookReview = bookReviewAssembler.saveBookReview(bookReview);
            return new ResponseEntity<>(savedBookReview, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookReviewById(@PathVariable Long id) {
        try {
            bookReviewAssembler.deleteBookReviewById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
