package ru.mrhellko.library.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.dao.BookDAO;
import ru.mrhellko.library.dao.BookReviewDAO;
import ru.mrhellko.library.dto.BookReviewByBookIdDTO;
import ru.mrhellko.library.dto.BookWithAverageRatingDTO;

import java.util.List;

@Service
public class BookAssembler {
    @Autowired
    private BookDAO bookDAO;
    @Autowired
    private BookReviewDAO bookReviewDAO;

    public BookWithAverageRatingDTO getFullBookWithAverageRatingDTO(Long id) {
        BookWithAverageRatingDTO bookWithAverageRatingDTO = bookDAO.getBookById(id);
        bookWithAverageRatingDTO.setAverageRating(this.getAverageRating(id));
        return bookWithAverageRatingDTO;
    }

    public List<BookWithAverageRatingDTO> getFullAllBooks() {
        List<BookWithAverageRatingDTO> bookWithAverageRatingDTOs = bookDAO.getAll();
        for (BookWithAverageRatingDTO bookWithAverageRatingDTO : bookWithAverageRatingDTOs) {
            bookWithAverageRatingDTO.setAverageRating(this.getAverageRating(bookWithAverageRatingDTO.getId()));
        }
        return bookWithAverageRatingDTOs;
    }

    public Float getAverageRating(long bookId) {
        List<BookReviewByBookIdDTO> bookReviews = bookReviewDAO.getReviewByBookId(bookId);
        int sum = 0;
        for (BookReviewByBookIdDTO bookReview : bookReviews) {
            sum += bookReview.getRating();
        }
        if (bookReviews.size() == 0)
            return null;
        else
            return (float) sum / bookReviews.size();
    }
}
