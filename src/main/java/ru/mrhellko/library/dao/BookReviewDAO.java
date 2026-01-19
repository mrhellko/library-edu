package ru.mrhellko.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.dto.BookReviewByBookIdDTO;
import ru.mrhellko.library.dto.BookReviewByReviewerNameDTO;

import java.util.List;

@Service
public class BookReviewDAO {
    private static final String GET_REVIEW_BY_ID_BOOK = "select * from book_reviews r where r.book_id = ?";
    private static final String GET_REVIEW_BY_REVIEWER_NAME = "select r.review_text, r.rating, b.book_name, b.author_name from book_reviews r inner join books b on b.id = r.book_id where r.reviewer_name = ?";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<BookReviewByBookIdDTO> getReviewByBookId(long bookId) {
        return jdbcTemplate.query(
                GET_REVIEW_BY_ID_BOOK,
                (resultSet, _)->{
                    final BookReviewByBookIdDTO bookReviewByBookIdDTO = new BookReviewByBookIdDTO();
                    bookReviewByBookIdDTO.setReviewerName(resultSet.getString("reviewer_name"));
                    bookReviewByBookIdDTO.setText(resultSet.getString("review_text"));
                    bookReviewByBookIdDTO.setRating(resultSet.getByte("rating"));
                    return bookReviewByBookIdDTO;
                },
                bookId
        );
    }

    public List<BookReviewByReviewerNameDTO> getReviewByReviewerName(String reviewerName) {
        return jdbcTemplate.query(
                GET_REVIEW_BY_REVIEWER_NAME,
                (resultSet, _)->{
                    final BookReviewByReviewerNameDTO bookReviewByReviewerNameDTO = new BookReviewByReviewerNameDTO();
                    bookReviewByReviewerNameDTO.setText(resultSet.getString("review_text"));
                    bookReviewByReviewerNameDTO.setRating(resultSet.getByte("rating"));
                    bookReviewByReviewerNameDTO.setBookName(resultSet.getString("book_name"));
                    bookReviewByReviewerNameDTO.setAuthor(resultSet.getString("author_name"));
                    return bookReviewByReviewerNameDTO;
                },
                reviewerName
        );
    }

    public Float getAverageRating(long book_id)
    {
        List<BookReviewByBookIdDTO> bookReviews = getReviewByBookId(book_id);
        int sum = 0;
        for(BookReviewByBookIdDTO bookReview : bookReviews)
        {
            sum+=bookReview.getRating();
        }
        return (float)sum/bookReviews.toArray().length;
    }
}
