package ru.mrhellko.library.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.dao.BookReviewDAO;
import ru.mrhellko.library.dto.BookReviewByBookIdDTO;
import ru.mrhellko.library.dto.BookReviewByReviewerNameDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookReviewAssembler {
    @Autowired
    BookReviewDAO bookReviewDAO;

    public ResponseEntity<List<BookReviewByBookIdDTO>> getReviewByBookId(Long bookId) {
        List<BookReview> bookReviews = bookReviewDAO.getReviewByBookId(bookId);
        if (!bookReviews.isEmpty()) {
            List<BookReviewByBookIdDTO> bookReviewByBookIdDTOS = new ArrayList<>();
            for (BookReview bookReview : bookReviews) {
                BookReviewByBookIdDTO bookReviewByBookIdDTO = new BookReviewByBookIdDTO(bookReview);
                bookReviewByBookIdDTOS.add(bookReviewByBookIdDTO);
            }
            return new ResponseEntity<>(bookReviewByBookIdDTOS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<List<BookReviewByReviewerNameDTO>> getReviewByReviewerName(String reviewerName) {
        List<BookReviewByReviewerNameDTO> bookReviewByReviewerNameDTOS = bookReviewDAO.getReviewByReviewerName(reviewerName);
        if (!bookReviewByReviewerNameDTOS.isEmpty()) {
            return new ResponseEntity<>(bookReviewByReviewerNameDTOS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<BookReview> updateBookReview(BookReview bookReview, Long id) {
        BookReview updatedBookReview = bookReviewDAO.getReviewById(id);
        if (updatedBookReview != null) {
            updatedBookReview.setId(id);
            updatedBookReview.setBookId(bookReview.getBookId());
            updatedBookReview.setRating(bookReview.getRating());
            updatedBookReview.setReviewerName(bookReview.getReviewerName());
            updatedBookReview.setReviewText(bookReview.getReviewText());

            bookReviewDAO.updateBookReview(updatedBookReview);

            return new ResponseEntity<>(updatedBookReview, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<BookReview> saveBookReview(BookReview bookReview) {
        try {
            BookReview savedBookReview = bookReviewDAO.saveBookReview(bookReview);
            return new ResponseEntity<>(savedBookReview, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Void> deleteBookReviewById(Long id) {
        try {
            int result = bookReviewDAO.deleteBookReviewById(id);
            if (result == 0) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
