package ru.mrhellko.library.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.dao.BookReviewDAO;
import ru.mrhellko.library.dto.BookReviewByBookIdDTO;
import ru.mrhellko.library.dto.BookReviewByReviewerNameDTO;
import ru.mrhellko.library.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookReviewAssembler {
    @Autowired
    BookReviewDAO bookReviewDAO;

    public List<BookReviewByBookIdDTO> getReviewByBookId(Long bookId) {
        List<BookReview> bookReviews = bookReviewDAO.getReviewByBookId(bookId);
        List<BookReviewByBookIdDTO> bookReviewByBookIdDTOS = new ArrayList<>();
        for (BookReview bookReview : bookReviews) {
            BookReviewByBookIdDTO bookReviewByBookIdDTO = new BookReviewByBookIdDTO(bookReview);
            bookReviewByBookIdDTOS.add(bookReviewByBookIdDTO);
        }
        return bookReviewByBookIdDTOS;
    }

    public List<BookReviewByReviewerNameDTO> getReviewByReviewerName(String reviewerName) {
        return bookReviewDAO.getReviewByReviewerName(reviewerName);
    }

    public BookReview updateBookReview(BookReview bookReview, Long id) {
        BookReview updatedBookReview = bookReviewDAO.getReviewById(id);
        if (updatedBookReview != null) {
            updatedBookReview.setId(id);
            updatedBookReview.setBookId(bookReview.getBookId());
            updatedBookReview.setRating(bookReview.getRating());
            updatedBookReview.setReviewerName(bookReview.getReviewerName());
            updatedBookReview.setReviewText(bookReview.getReviewText());

            bookReviewDAO.updateBookReview(updatedBookReview);

            return updatedBookReview;
        } else {
            return null;
        }
    }

    public BookReview saveBookReview(BookReview bookReview) throws Exception {
        return bookReviewDAO.saveBookReview(bookReview);
    }

    public void deleteBookReviewById(Long id) throws Exception {
        int result = bookReviewDAO.deleteBookReviewById(id);
        if (result == 0) {
            throw new NotFoundException(id);
        }
    }
}
