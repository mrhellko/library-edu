package ru.mrhellko.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.BookReview;
import ru.mrhellko.library.dto.BookReviewByReviewerNameDTO;

import java.util.List;

@Service
public class BookReviewDAO {
    private static final String GET_REVIEW_BY_ID_SQL = "select r.id, r.book_id, r.rating, r.reviewer_name, r.review_text from book_reviews r where r.id = ?";
    private static final String GET_REVIEW_BY_ID_BOOK_SQL = "select r.id, r.book_id, r.rating, r.reviewer_name, r.review_text from book_reviews r where r.book_id = ?";
    private static final String GET_REVIEW_BY_REVIEWER_NAME_SQL = "select r.review_text, r.rating, b.book_name, b.author_name from book_reviews r inner join books b on b.id = r.book_id where r.reviewer_name = ?";
    private static final String UPDATE_REVIEW_BY_ID_SQL = "update book_reviews set book_id = ?, rating = ?, reviewer_name = ?, review_text = ? where id = ?";
    private static final String SAVE_REVIEW_SQL = "insert into book_reviews (id, book_id, rating, reviewer_name, review_text) values (nextval('book_reviews_seq'), ?, ?, ?, ?)";
    private static final String GET_LAST_REVIEW_SQL = "select r.id, r.book_id, r.rating, r.reviewer_name, r.review_text from book_reviews r order by id desc limit 1";
    private static final String DELETE_REVIEW_BY_ID_SQL = "delete from book_reviews where id = ?";
    private static final String DELETE_REVIEW_BY_BOOK_ID_SQL = "delete from book_reviews where book_id = ?";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<BookReview> bookReviewRowMapper = (resultSet, _) -> {
        final BookReview bookReview = new BookReview();
        bookReview.setId(resultSet.getLong("id"));
        bookReview.setBookId(resultSet.getLong("book_id"));
        bookReview.setRating(resultSet.getByte("rating"));
        bookReview.setReviewerName(resultSet.getString("reviewer_name"));
        bookReview.setReviewText(resultSet.getString("review_text"));
        return bookReview;
    };
    private final RowMapper<BookReviewByReviewerNameDTO> bookReviewByReviewerNameDTORowMapper = (resultSet, _) -> {
        final BookReviewByReviewerNameDTO bookReviewByReviewerNameDTO = new BookReviewByReviewerNameDTO();
        bookReviewByReviewerNameDTO.setReviewText(resultSet.getString("review_text"));
        bookReviewByReviewerNameDTO.setRating(resultSet.getByte("rating"));
        bookReviewByReviewerNameDTO.setBookName(resultSet.getString("book_name"));
        bookReviewByReviewerNameDTO.setAuthorName(resultSet.getString("author_name"));
        return bookReviewByReviewerNameDTO;
    };

    public BookReview getReviewById(long id) {
        try {
            return jdbcTemplate.queryForObject(GET_REVIEW_BY_ID_SQL, bookReviewRowMapper, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public List<BookReview> getReviewByBookId(long bookId) {

        return jdbcTemplate.query(
                GET_REVIEW_BY_ID_BOOK_SQL,
                bookReviewRowMapper,
                bookId
        );
    }

    public List<BookReviewByReviewerNameDTO> getReviewByReviewerName(String reviewerName) {
        return jdbcTemplate.query(
                GET_REVIEW_BY_REVIEWER_NAME_SQL,
                bookReviewByReviewerNameDTORowMapper,
                reviewerName
        );
    }

    public int updateBookReview(BookReview bookReview) {
        return jdbcTemplate.update(UPDATE_REVIEW_BY_ID_SQL, new Object[]{bookReview.getBookId(), bookReview.getRating(), bookReview.getReviewerName(), bookReview.getReviewText(), bookReview.getId()});
    }

    public BookReview saveBookReview(BookReview bookReview) {
        jdbcTemplate.update(SAVE_REVIEW_SQL, new Object[]{bookReview.getBookId(), bookReview.getRating(), bookReview.getReviewerName(), bookReview.getReviewText()});
        return jdbcTemplate.queryForObject(GET_LAST_REVIEW_SQL, bookReviewRowMapper);
    }

    public int deleteBookReviewById(Long id) {
        return jdbcTemplate.update(DELETE_REVIEW_BY_ID_SQL, id);
    }

    public int deleteBookReviewByBookId(Long bookId) {
        return jdbcTemplate.update(DELETE_REVIEW_BY_BOOK_ID_SQL, bookId);
    }
}
