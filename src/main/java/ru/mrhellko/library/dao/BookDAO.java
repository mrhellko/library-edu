package ru.mrhellko.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.dto.BookWithAverageRatingDTO;


import java.util.List;

@Service
public class BookDAO {

    private static final String GET_BOOK_BY_ID_SQL = "select * from books b where b.id = ?";
    private static final String GET_ALL_BOOK = "select * from books";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BookReviewDAO bookReviewDAO;
    private final RowMapper<BookWithAverageRatingDTO> bookRowMapper = (resultSet, _) -> {
        final BookWithAverageRatingDTO book = new BookWithAverageRatingDTO();
        book.setId(resultSet.getLong("id"));
        book.setBookName(resultSet.getString("book_name"));
        book.setAuthor(resultSet.getString("author_name"));
        book.setAverageRating(bookReviewDAO.getAverageRating(book.getId()));
        return book;
    };

    public BookWithAverageRatingDTO getBookById(long id) {
        return jdbcTemplate.queryForObject(
                GET_BOOK_BY_ID_SQL,
                bookRowMapper,
                id
        );
    }

    public List<BookWithAverageRatingDTO> getAll() {
        return jdbcTemplate.query(GET_ALL_BOOK, bookRowMapper);
    }
}
