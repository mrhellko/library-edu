package ru.mrhellko.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrhellko.library.dao.BookReviewDAO;
import ru.mrhellko.library.dto.BookReviewByBookIdDTO;
import ru.mrhellko.library.dto.BookReviewByReviewerNameDTO;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private BookReviewDAO bookReviewDAO;

    @GetMapping("/book/{bookId}")
    public List<BookReviewByBookIdDTO> getReviewByBookId(@PathVariable Long bookId) {
        return bookReviewDAO.getReviewByBookId(bookId);
    }

    @GetMapping("/reviewer/{reviewerName}")
    public List<BookReviewByReviewerNameDTO> getReviewByReviewerName(@PathVariable String reviewerName) {
        return bookReviewDAO.getReviewByReviewerName(reviewerName);
    }
}
