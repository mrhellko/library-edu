package ru.mrhellko.library.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.mrhellko.library.Entity.Book;


import java.util.List;

@Service
public class BookDAO {

    private static final String GET_BOOK_BY_ID_SQL = "select b.id, b.book_name, b.author_name from books b where b.id = ?";
    private static final String GET_ALL_BOOK_SQL = "select b.id, b.book_name, b.author_name from books b";
    private static final String UPDATE_BOOK_BY_ID_SQL = "update books set book_name = ?, author_name = ? where id = ?";
    private static final String SAVE_BOOK_SQL = "insert into books (id, book_name, author_name) values (?, ?, ?)";
    private static final String DELETE_BOOK_BY_ID_SQL = "delete from books where id = ?";
    private static final String GET_BOOKS_BY_AUTHOR_NAME = "select b.id, b.book_name, b.author_name from books b where b.author_name ilike '%' || ? || '%'";
    private static final String GET_NEXT_SEQUENCE_ID_SQL = "select nextval('books_seq') as id";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<Book> bookRowMapper = (resultSet, _) -> {
        final Book book = new Book();
        book.setId(resultSet.getLong("id"));
        book.setBookName(resultSet.getString("book_name"));
        book.setAuthor(resultSet.getString("author_name"));
        return book;
    };
    private final RowMapper<Long> idRowMapper = (resultSet, _) -> (Long) resultSet.getLong("id");

    public Book getBookById(long id) {
        try {
            return jdbcTemplate.queryForObject(
                    GET_BOOK_BY_ID_SQL,
                    bookRowMapper,
                    id
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public List<Book> getAll() {
        return jdbcTemplate.query(GET_ALL_BOOK_SQL, bookRowMapper);
    }

    public void updateBook(Book book) {
        jdbcTemplate.update(UPDATE_BOOK_BY_ID_SQL, book.getBookName(), book.getAuthor(), book.getId());
    }

    public Book saveBook(Book book) throws Exception {
        book.setId(jdbcTemplate.queryForObject(GET_NEXT_SEQUENCE_ID_SQL, idRowMapper));
        jdbcTemplate.update(SAVE_BOOK_SQL, book.getId(), book.getBookName(), book.getAuthor());
        return book;
    }

    public int deleteBookById(Long id) throws Exception {
        return jdbcTemplate.update(DELETE_BOOK_BY_ID_SQL, id);
    }

    public List<Book> getBooksByAuthorName(String authorName) {
        return jdbcTemplate.query(GET_BOOKS_BY_AUTHOR_NAME, bookRowMapper, authorName);
    }
}
