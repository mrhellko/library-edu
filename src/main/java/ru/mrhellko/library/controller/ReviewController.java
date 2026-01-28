package ru.mrhellko.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.assembler.BookReviewAssembler;
import ru.mrhellko.library.dto.BookReviewByBookIdDTO;
import ru.mrhellko.library.dto.BookReviewByReviewerNameDTO;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private BookReviewAssembler bookReviewAssembler;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookReviewByBookIdDTO>> getReviewByBookId(@PathVariable Long bookId) {
        return bookReviewAssembler.getReviewByBookId(bookId);
    }

    @GetMapping("/reviewer/{reviewerName}")
    public ResponseEntity<List<BookReviewByReviewerNameDTO>> getReviewByReviewerName(@PathVariable String reviewerName) {
        return bookReviewAssembler.getReviewByReviewerName(reviewerName);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookReview> updateBookReview(@RequestBody BookReview bookReview, @PathVariable Long id) {
        return bookReviewAssembler.updateBookReview(bookReview, id);
    }

    @PostMapping("/")
    public ResponseEntity<BookReview> saveBookReview(@RequestBody BookReview bookReview) {
        return bookReviewAssembler.saveBookReview(bookReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookReviewById(@PathVariable Long id) {
        return bookReviewAssembler.deleteBookReviewById(id);
    }
}
